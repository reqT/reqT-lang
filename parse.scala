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
    val isEnd = this.isInstanceOf[Token.End]
    val isDelim = isIndent || isEnd
    val isSpace = this.isInstanceOf[Token.Space]
    val isWord = this.isInstanceOf[Token.Word]


  object Token:
    case class Word(text: String)(val line: Int, val orig: String) extends Token
    case class Num(i: Int)(val line: Int, val orig: String) extends Token
    case class Space(nbrSpaces: Int)(val line: Int, val orig: String) extends Token
    case class Indent(level: Int)(val line: Int, val orig: String) extends Token
    case class End()(val line: Int, val orig: String = "") extends Token

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
        buf += Token.End()(currentLineNbr + 1)
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

  extension (tokens: List[Token]) def toText: String = tokens.map(_.orig).mkString

  def partitionNextLine(tokens: List[Token]): (List[Token], List[Token]) = 
    tokens match 
    case Nil | List(Token.End()) | List(Token.Indent(_)) => (Nil, Nil)
    case List(Token.Indent(_), Token.End()) => (Nil, Nil)
    case List(Token.Indent(_), x@Token.Indent(_), xs*) => partitionNextLine(x :: xs.toList)
    case List(x@Token.Indent(level), xs*) => 
      (x +: xs.toList.takeWhile(!_.isDelim), xs.toList.dropWhile(!_.isDelim))
    case xs => assert(false, s"reqt.parse bug: Indent|End missing in $xs")

  def parseModel(tokens: List[Token]): Either[(Token, String), List[Elem]] = 
    val (nextLine, rest) = partitionNextLine(tokens)
    nextLine match
    case Nil => Right(List())
    case List(Token.Indent(_))  => parseModel(rest)
    case List(Token.End()) => Right(List())
    case List(indent@Token.Indent(level), line*) =>
       line match
       case Nil => parseModel(rest)
       case x :: xs if x.isStrAttrType => Right(List(x.sat.apply(xs.toText)))
       case x :: xs if x.isIntAttrType => 
         xs.headOption match
         case Some(Token.Num(i)) => Right(List(x.iat.apply(i)))
         case Some(t) => Left(t -> s"integer expected after ${x.iat}")
         case None => Left(x -> s"integer expected but end of line found")
       
       case e :: id :: xs => 
          ??? // kolla om xs slutar med relType och allt där emellan blir id och ta submodel i början av rest med större idndentering
       case e :: xs if e.isEntType => Right(List(e.ent.apply(xs.toText)))
       case x :: xs => Left(x -> s"element type expected")

    case xs => assert(false, s"reqt.parse bug: Indent missing in $xs")

  extension (s: String) def toModel: Either[String, Model] = 
    parseModel(s.tokenize) match
    case Right(elems) => Right(Model(elems*))
    case Left((t, msg)) => Left(s"error at line ${t.line}: ${msg};${ t.orig}")

end parse