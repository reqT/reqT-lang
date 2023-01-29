package reqt

import reqt.lang.*

object parse:
  val parseEntType: Map[String, EntType] = 
    EntType.values.map(e => e.toString -> e).toMap.withDefaultValue(null) //ugly but fast

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

  object Token:
    case class Word(s: String)(val line: Int, val orig: String) extends Token
    case class Num(i: Int)(val line: Int, val orig: String) extends Token
    case class Space(nbrSpaces: Int)(val line: Int, val orig: String) extends Token
    case class Indent(level: Int)(val line: Int, val orig: String) extends Token
    case class Err(msg: String)(val line: Int, val orig: String) extends Token
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
        buf.toList
  
  def mergeWords(tokens: List[Token]): List[Token] = tokens match
    case List(w1@Token.Word(s1), sp@Token.Space(n), w2@Token.Word(s2), xs*) if !w1.isElemType && !w2.isElemType =>
      mergeWords(Token.Word(s"$s1${sp.orig}$s2")(w1.line, w1.orig + sp.orig + w2.orig) +: xs.toList)  
    case x :: xs => x :: mergeWords(xs)
    case Nil => Nil
  
  def parseModel(tokens: List[Token]): Either[String, lang.Model] = 
    if tokens.isEmpty then Right(lang.Model()) else
      var builder = lang.ModelBuilder()
      Right(builder.toModel)