package reqt

object parse:
  val parseEntType: Map[String, EntType] = //null is ugly but fast
    EntType.values.map(e => e.toString -> e).toMap.withDefaultValue(null) 

  val parseStrAttrType: Map[String, StrAttrType] = 
    StrAttrType.values.map(e => e.toString -> e).toMap.withDefaultValue(null)

  val parseIntAttrType: Map[String, IntAttrType] = 
    IntAttrType.values.map(e => e.toString -> e).toMap.withDefaultValue(null)

  val parseRelType: Map[String, RelType] = 
    RelType.values.map(e => e.toString.toLowerCase -> e).toMap.withDefaultValue(null)

  sealed trait Token:
    def line: Int
    def orig: String
    
    lazy val ent: EntType = parseEntType(orig)
    lazy val isEntType = ent != null

    lazy val sat = parseStrAttrType(orig)
    def isStrAttrType = sat != null 
    
    lazy val iat = parseIntAttrType(orig)
    lazy val isIntAttrType = iat != null 
    
    lazy val ret: RelType = parseRelType(orig)
    lazy val isRelType = ret != null

    lazy val isElemType = isEntType || isIntAttrType || isStrAttrType || isRelType

    val isIndent = this.isInstanceOf[Token.Indent]
    val isNum = this.isInstanceOf[Token.Num]
    val isSpace = this.isInstanceOf[Token.Space]
    val isWord = this.isInstanceOf[Token.Word]


  object Token:
    case class Word(text: String)(val line: Int, val orig: String) extends Token
    case class Num(i: Int)(val line: Int, val orig: String) extends Token
    case class Space(nbrSpaces: Int)(val line: Int, val orig: String) extends Token
    case class Indent(level: Int)(val line: Int, val orig: String) extends Token
    //case class End()(val line: Int, val orig: String = "") extends Token

  val tabSpaces = " " * 2

  extension (s: String) 
    def replaceTabsWithSpace: String = s.replace("\t", tabSpaces)

    def splitIntoWordsAndSpaces: List[String] = 
      if s.isEmpty then List() else
        val buf = scala.collection.mutable.ListBuffer.empty[String]
        var pos = 0
        var isInsideWord = !s(0).isWhitespace
        while pos < s.length do
          val nextPos = s.indexWhere(c => if isInsideWord then c.isWhitespace else !c.isWhitespace, pos)
          if nextPos == -1 then
            buf += s.substring(pos)
            pos = s.length
          else 
            buf += s.substring(pos, nextPos)
            pos = nextPos
            isInsideWord = !isInsideWord
        buf.toList

    def tokenize: List[Token] = 
      val xs = s.splitIntoWordsAndSpaces
      if xs.isEmpty then List() else
        val it = xs.iterator
        var currentLineNbr = 1
        val buf = collection.mutable.ListBuffer[Token](Token.Indent(0)(1, ""))
        while it.hasNext do
          val s = it.next
          if s.nonEmpty && s(0).isWhitespace then
            val nbrNewLines = s.count(_ == '\n')
            if nbrNewLines == 0 then buf += Token.Space(s.length)(currentLineNbr, s)
            else 
              currentLineNbr += nbrNewLines
              if s.endsWith("\n") then buf += Token.Indent(0)(currentLineNbr, s)
              else // s ends with spaces before newline 
                val level = s.reverse.takeWhile(_ != '\n').replaceTabsWithSpace.length
                buf += Token.Indent(level)(currentLineNbr, s)
          else
            val i = s.trim.toIntOption
            if i.isDefined then buf += Token.Num(i.get)(currentLineNbr, s)   
            else buf += Token.Word(s)(currentLineNbr, s)
        end while
        //buf += Token.End()(currentLineNbr + 1)
        mergeWords(buf.toList).filterNot(_.isSpace)

  end extension 

  /** combine pairs of non-ElemType words */
  def mergeWords(tokens: List[Token]): List[Token] = 
    tokens match
    case List(w1@Token.Word(s1), sp@Token.Space(n), w2@Token.Word(s2), xs*) 
      if !w1.isElemType && !w2.isElemType =>
        mergeWords( 
          Token.Word(s"$s1${sp.orig}$s2")(w1.line, w1.orig + sp.orig + w2.orig) 
            +: xs.toList)
    case Nil => Nil
    case x :: xs => x :: mergeWords(xs)

  extension (tokens: List[Token]) 
    def toText: String = tokens.map(_.orig).mkString

  def partitionNextLine(tokens: List[Token]): (List[Token], List[Token]) = 
    tokens match 
    case Nil | List(Token.Indent(_)) => (Nil, Nil)
    case List(Token.Indent(_), x@Token.Indent(_), xs*) => partitionNextLine(x :: xs.toList)
    case List(x@Token.Indent(level), xs*) => 
      (x +: xs.toList.takeWhile(!_.isIndent), xs.toList.dropWhile(!_.isIndent))
    case xs => assert(false, s"reqt.parse bug: Indent|End missing in $xs")

  extension (t: Token) 
    def isIndentSameOrLeftOf(currentLevel: Int): Boolean = 
      t match
      case Token.Indent(myLevel) if myLevel <= currentLevel => true 
      case _ => false

  type ParseResult = Either[(Token, String), List[Elem]]

  def parseStrAttr(sat: StrAttrType, current: Token, remaining: List[Token]): ParseResult = 
    val remainingTokensOnThisLine = remaining.takeWhile(!_.isIndent) 
    val strAttr =  sat.apply(remainingTokensOnThisLine.toText)
    val remainingTokensAfterThisLine = remaining.drop(remainingTokensOnThisLine.length)
    val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
    remainingElemsOrErr match 
    case Right(elems) => Right(strAttr :: elems)
    case Left(err) => Left(err)

  def parseIntAttr(iat: IntAttrType, current: Token, remaining: List[Token]): ParseResult = 
    remaining match
    case List(n@Token.Num(i), ys*) => 
      val remainingTokensOnThisLine = ys.toList.takeWhile(!_.isIndent)
      val intAttr = iat.apply(n.i)
      val remainingTokensAfterThisLine = ys.toList.drop(remainingTokensOnThisLine.length)
      val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
      remainingElemsOrErr match 
      case Right(elems) => Right(intAttr :: elems)
      case Left(err)    => Left(err)
    case List(t, ts*) => Left(t -> s"integer expected after $iat")
    case Nil => Left(current -> s"integer expected but end of line found")

  def parseMultiLineRelation(ret: RelType, ent: EntType, id: Token, remaining: List[Token], remainingOnThisLine: List[Token], level: Int): ParseResult =
    val subTokens = remaining.drop(remainingOnThisLine.length).takeWhile(!_.isIndentSameOrLeftOf(level))
    println(s"  >>> subTokens = $subTokens")
    val subElemsOrErr = parseElems(subTokens)
    subElemsOrErr match 
    case Right(elems) => 
      val rel = Rel(ent.apply(id.orig), ret, Model(elems*))
      val remainingTokens = remaining.drop(remainingOnThisLine.length + subTokens.length)
      val remainingElemsOrErr = parseElems(remainingTokens)
      remainingElemsOrErr match
        case Right(moreElems) => Right(rel :: moreElems)
        case left => left 
    case left => left

  def parseOneLineRelation(ent: EntType, id: Token, remaining: List[Token], 
    remainingOnThisLine: List[Token], 
    reversedRemainingOnThisLine: List[Token], 
    lastTokenOnThisLine: Token
  ): ParseResult =
    val ret  = reversedRemainingOnThisLine(2).ret
    val atToken = reversedRemainingOnThisLine(1)
    val isLegalAttr = atToken.isIntAttrType || atToken.isStrAttrType
    if !isLegalAttr then Left(atToken -> "attribute type expected") 
    else 
      val at: AttrType = if atToken.isIntAttrType then atToken.iat else atToken.sat
      println(s"  !!!!! parseOneLineRelation  at=$at")
      val a = lastTokenOnThisLine match
        case Token.Num(i) if atToken.isIntAttrType => atToken.iat.apply(i)
        case _ if atToken.isStrAttrType => atToken.sat.apply(lastTokenOnThisLine.orig) 
        case t => println(s"????????? $t"); ???
      
      val remainingTokensAfterThisLine = remaining.drop(remainingOnThisLine.length)
      val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
      remainingElemsOrErr match
      case Right(elems) => 
        // TODO take indented lines after this from elems and put in submodel
        Right(Rel(ent.apply(id.orig), ret , Model(a)) :: elems)
      case Left(err) => Left(err)

  def parseSingleEntity(ent: EntType, id: Token, xs: List[Token], remainingOnThisLine: List[Token]): ParseResult =
    // make an entity with the rest of line part of id
    val remainingTokensAfterThisLine = xs.drop(remainingOnThisLine.length)
    val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
    remainingElemsOrErr match
    case Right(elems) => Right(ent.apply((id :: remainingOnThisLine).toText):: elems)
    case Left(err) => Left(err)


  def parseElems(tokens: List[Token]): ParseResult = 
    println(s">>>  parsing $tokens")
    tokens match
    case Nil => Right(Nil)
    
    case List(Token.Indent(_)) => Right(Nil)

    case List(Token.Indent(_), i@Token.Indent(_), elems*) => // skip first indent
      parseElems(i :: elems.toList) 

    case List(indent@Token.Indent(level), tokensAfterIndent*) =>
      tokensAfterIndent.toList match
      case Nil => Right(Nil)
      case x :: xs if x.isStrAttrType => parseStrAttr(x.sat, x, xs)
      case x :: xs if x.isIntAttrType => parseIntAttr(x.iat, x, xs)
      case e :: Nil if e.isEntType => Left(e -> s"id expected after ${e.ent}")
      case e :: id :: xs if e.isEntType && id.isElemType => Left(e -> s"id expected after ${e.ent} but elem found")
      case e :: id :: xs if e.isEntType => // parse entity with legal id
        // TODO: check that id does not contain whitespace,
        val remainingOnThisLine = xs.takeWhile(!_.isIndent)
        if remainingOnThisLine.nonEmpty then 
          val reversedRemainingOnThisLine = remainingOnThisLine.reverse
          val lastOnThisLine = reversedRemainingOnThisLine.head 
          println(s"  >>> xs = $xs")
          println(s"  >>> lastOnThisLine = $lastOnThisLine")
          if lastOnThisLine.isRelType  
          then parseMultiLineRelation(lastOnThisLine.ret, e.ent, id, xs, remainingOnThisLine, level)
          else if lastOnThisLine.isNum // a hack to allow one-liners: Feature x has Prio 1
                  && remainingOnThisLine.length > 2 
                  //&& reversedRemainingOnThisLine(1).isIntAttrType 
                  && reversedRemainingOnThisLine(2).isRelType  
          then parseOneLineRelation(e.ent, id, xs, remainingOnThisLine, reversedRemainingOnThisLine, lastOnThisLine)
          else { println("  >>>--- parseSingleEntity more on this line"); parseSingleEntity(e.ent, id, xs, remainingOnThisLine) }
          end if
        else { println("  >>>--- parseSingleEntity Nil on this line"); parseSingleEntity(e.ent, id, xs, Nil) }
          
          // // this line is single entity and id and nothing else
          // val remainingTokensAfterThisLine = xs.drop(remainingOnThisLine.length)
          // val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
          // remainingElemsOrErr match
          // case Right(elems) => Right(e.ent.apply(id.orig) :: elems)
          // case Left(err) => Left(err)
      
      case x :: xs => Left(x -> s"element type expected before $xs")

    case xs => assert(false, s"reqt.parse bug: Indent or End token missing in $xs")

  extension (s: String) def toModel: Either[String, Model] = 
    parseElems(s.tokenize) match
    case Right(elems) => Right(Model(elems*))
    case Left((t, msg)) => Left(s"error at line ${t.line}: ${msg};${ t.orig}")
