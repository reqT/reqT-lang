package reqt

import reqt.lang.*

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

  type ParseResult = Either[(Token, String), List[Elem]]

  extension (t: Token) 
    def isIndentSameOrLeftOf(currentLevel: Int): Boolean = 
      t match
      case Token.Indent(myLevel) if myLevel <= currentLevel => true 
      case _ => false

  def parseElems(tokens: List[Token]): ParseResult = 
    println(s">>>  parsing $tokens")
    tokens match
    case Nil => Right(Nil)
    case List(Token.Indent(_)) => Right(Nil)
    case List(Token.Indent(_), i@Token.Indent(_), elems*) => parseElems(i :: elems.toList)
    case List(indent@Token.Indent(level), tokensAfterIndent*) =>
       val tokensToParse = tokensAfterIndent.toList
       tokensToParse match
       case Nil => Right(Nil)

       case x :: xs if x.isStrAttrType =>
         val remainingTokensOnThisLine = xs.takeWhile(!_.isIndent) 
         val strAttr =  x.sat.apply(remainingTokensOnThisLine.toText)
         val remainingTokensAfterThisLine = xs.drop(remainingTokensOnThisLine.length)
         val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
         remainingElemsOrErr match 
         case Right(elems) => Right(strAttr :: elems)
         case Left(err) => Left(err)
       
       case x :: xs if x.isIntAttrType => 
         xs match
         case List(n@Token.Num(i), ys*) => 
           val remainingTokensOnThisLine = ys.toList.takeWhile(!_.isIndent)
           val intAttr = x.iat.apply(n.i)
           val remainingTokensAfterThisLine = ys.toList.drop(remainingTokensOnThisLine.length)
           val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
           remainingElemsOrErr match 
           case Right(elems) => Right(intAttr :: elems)
           case Left(err)    => Left(err)
         case List(t, ts*) => Left(t -> s"integer expected after ${x.iat}")
         case Nil => Left(x -> s"integer expected but end of line found")

       case e :: Nil if e.isEntType => Left(e -> s"id expected")

       case e :: id :: xs if e.isEntType && id.isElemType => Left(e -> s"id expected but elem found")

       case e :: id :: xs if e.isEntType => // Feature xyz has ...
          val remainingTokensOnThisLine = xs.takeWhile(!_.isIndent)
          if remainingTokensOnThisLine.nonEmpty then 
            val reversedRemainingTokensOnThisLine = remainingTokensOnThisLine.reverse
            val lastTokenOnThisLine = reversedRemainingTokensOnThisLine.head 
            println(s"  >>> xs = $xs")
            println(s"  >>> lastTokenOnThisLine = $lastTokenOnThisLine")
            if lastTokenOnThisLine.isRelType then
              val relType = lastTokenOnThisLine.ret
              val subTokens = xs.drop(remainingTokensOnThisLine.length).takeWhile(!_.isIndentSameOrLeftOf(level))
              println(s"  >>> subTokens = $subTokens")
              val subElemsOrErr = parseElems(subTokens)
              subElemsOrErr match 
              case Right(elems) => 
                val rel = Rel(e.ent.apply(id.orig), relType, Model(elems*))
                val remainingTokens = xs.drop(remainingTokensOnThisLine.length + subTokens.length)
                val remainingElemsOrErr = parseElems(remainingTokens)
                remainingElemsOrErr match
                  case Right(moreElems) => Right(rel :: moreElems)
                  case left => left 
              case left => left
            else if lastTokenOnThisLine.isNum // a hack to allow one-liners: Feature x has Prio 1
                    && remainingTokensOnThisLine.length > 2 
                    && reversedRemainingTokensOnThisLine(1).isIntAttrType 
                    && reversedRemainingTokensOnThisLine(2).isRelType then 
              val ret  = reversedRemainingTokensOnThisLine(2).ret
              val iat = reversedRemainingTokensOnThisLine(1).iat
              val i = lastTokenOnThisLine.asInstanceOf[Token.Num].i
              val remainingTokensAfterThisLine = xs.drop(remainingTokensOnThisLine.length)
              val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
              remainingElemsOrErr match
              case Right(elems) => 
                // TODO take indented lines after this from elems and put in submodel
                Right(Rel(e.ent.apply(id.orig), ret , Model(iat.apply(i))) :: elems)
              case Left(err) => Left(err)

            else // make an entity with the rest of line part of id
              val remainingTokensAfterThisLine = xs.drop(remainingTokensOnThisLine.length)
              val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
              remainingElemsOrErr match
              case Right(elems) => Right(e.ent.apply((id :: remainingTokensOnThisLine).toText):: elems)
              case Left(err) => Left(err)

          else // this line is single entity and id and nothing else
            val remainingTokensAfterThisLine = xs.drop(remainingTokensOnThisLine.length)
            val remainingElemsOrErr = parseElems(remainingTokensAfterThisLine)
            remainingElemsOrErr match
            case Right(elems) => Right(e.ent.apply(id.orig) :: elems)
            case Left(err) => Left(err)
       
       case x :: xs => Left(x -> s"element type expected before $xs")

    case xs => assert(false, s"reqt.parse bug: Indent or End token missing in $xs")

  extension (s: String) def toModel: Either[String, Model] = 
    parseElems(s.tokenize) match
    case Right(elems) => Right(Model(elems*))
    case Left((t, msg)) => Left(s"error at line ${t.line}: ${msg};${ t.orig}")

end parse