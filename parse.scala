package reqt

import reqt.lang.*

object parse:
  val parseEntityType: Map[String, EntityType] = 
    EntityType.values.map(e => (e.toString, e)).toMap.withDefaultValue(null) //null is ugly but fast

  val parseAttributeType: Map[String, AttributeType] = 
    (StringAttributeType.values ++ IntAttributeType.values)
      .map(e => (e.toString, e)).toMap.withDefaultValue(null)

  val parseRelationType: Map[String, RelationType] = 
    RelationType.values.map(e => (e.toString.toLowerCase, e)).toMap.withDefaultValue(null)

  // val isEntity: Set[String] = meta.entityConcepts.map(_._1).toSet
  // val isStringAttribute: Set[String] = meta.stringAttributeConcepts.map(_._1).toSet
  // val isIntAttribute: Set[String] = meta.intAttributeConcepts.map(_._1).toSet
  // val isAttribute: Set[String] = isStringAttribute ++ isIntAttribute

  enum Token: 
    def lineNbr: Int
    def orig: String
    case Txt(s: String, lineNbr: Int, orig: String)
    case Num(i: Int, lineNbr: Int, orig: String)
    case Space(nbrOfSpaces: Int, lineNbr: Int, orig: String)
    case Indent(level: Int, lineNbr: Int, orig: String)
    case Ent(e: EntityType, lineNbr: Int, orig: String)
    case Att(a: AttributeType, lineNbr: Int, orig: String)
    case Rel(r: RelationType, lineNbr: Int, orig: String)

  val tabAsSpace = " " * 2

  extension (s: String) 
    def replaceTabsWithSpace: String = s.replace("\t", tabAsSpace)

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
        val buf = scala.collection.mutable.ListBuffer.empty[Token]
        while it.hasNext do
          val s = it.next
          if s.nonEmpty && s(0).isWhitespace then
            val nbrNewLines = s.count(_ == '\n')
            if nbrNewLines == 0 then buf += Token.Space(s.length, currentLineNbr, s)
            else 
              currentLineNbr += nbrNewLines
              if s.endsWith("\n") then buf += Token.Indent(0, currentLineNbr, s)
              else // s ends with a \n and then some leading spaces of indentation 
                val level = s.reverse.takeWhile(_ != '\n').replaceTabsWithSpace.length
                buf += Token.Indent(level, currentLineNbr, s)
          else
            val et = parseEntityType(s)
            if et != null then buf += Token.Ent(et, currentLineNbr, s) 
            else 
              val at = parseAttributeType(s)
              if at != null then buf += Token.Att(at, currentLineNbr, s) 
              else
                val rt = parseRelationType(s)
                if rt != null then buf += Token.Rel(rt, currentLineNbr, s) 
                else 
                  val i = s.trim.toIntOption
                  if i.isDefined then buf += Token.Num(i.get, currentLineNbr, s)   
                  else 
                    buf += Token.Txt(s, currentLineNbr, s)
        end while
        buf.toList

  
  def parseModel(tokens: List[Token]): Either[String, lang.Model] = 
    if tokens.isEmpty then Right(lang.Model()) else
      var builder = lang.ModelBuilder()
      Right(builder.toModel)