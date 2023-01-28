package reqt

import lang.*
import javax.management.relation.RelationType

object parse:
  val parseEntityType: Map[String, EntityType] = 
    EntityType.values.map(e => (e.toString, e)).toMap.withDefaultValue(null)

  val isEntity: Set[String] = meta.entityConcepts.map(_._1).toSet
  val isStringAttribute: Set[String] = meta.stringAttributeConcepts.map(_._1).toSet
  val isIntAttribute: Set[String] = meta.intAttributeConcepts.map(_._1).toSet
  val isAttribute: Set[String] = isStringAttribute ++ isIntAttribute

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

    def parts: List[String] = if s.isEmpty then List() else
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

  extension (xs: List[String]) def tokens: List[Token] = 
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
          else buf += Token.Txt(s, currentLineNbr, s)
      end while
      buf.toList

  
  def stringToModel(s: String): lang.Model = if s.isEmpty then lang.Model() else
    var builder = lang.ModelBuilder()
    val ts = s.parts.tokens
    builder.toModel