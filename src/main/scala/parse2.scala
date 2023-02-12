package reqt

import meta.*

object ModelParser:
  extension (s: String)
    def toLines: Array[String] = s.split("\n")
    def toWords: Array[String] = s.split(" ").map(_.trim).filter(_.nonEmpty)
    def skipIndent: String     = s.dropWhile(ch => ch.isSpaceChar || ch == '\t')
    def skipFirstWord: String  = s.dropWhile(ch => !(ch.isSpaceChar || ch == '\t'))
    def skipFirstToken: String = s.skipIndent.skipFirstWord.trim
    def level: Int = 
      val initSpace = s.takeWhile(ch => ch.isSpaceChar || ch == '\t')
      initSpace.replace("\\t", "  ").length

  def parse(input: String): Model = 
    val lines = input.toLines
    Model(parseLines(0, lines.length, lines*)*)

  def parseLines(fromIndex: Int, untilIndex: Int, lines: String*): List[Elem] = 
    val elems = List.empty[Elem].toBuffer
    var i = fromIndex
    while i < untilIndex do

      val line: String = lines(i)
      val level: Int = line.level
      val words: Array[String] = line.toWords

      if words.nonEmpty then

        val first = words.head
        val level = first.level
        val restOfLine = line.skipFirstToken

        inline def indexOfLastFollowingLineWithHigherIndentLevel: Int =
          var more = i
          while more + 1 < lines.length && (lines(more + 1).level > level) do more += 1
          more 

        inline def restPlusExtraLines(rest: String, more: Int): String =
          if more > i then 
            val extra = lines.slice(i + 1, more + 1).mkString("\n")
            i = i + more
            s"$rest\n$extra"
          else rest 

        inline def parseFollowingIndentedLines(rest: String): String =
          restPlusExtraLines(rest, indexOfLastFollowingLineWithHigherIndentLevel)

        first match
          case f if first.isStrAttrType =>  // parse StrAttr
            val value: String = parseFollowingIndentedLines(restOfLine)
            val sat: Attr[String] = strAttrTypes(f).apply(value)
            elems.append(sat)
            
          case f if first.isIntAttrType => // parse IntAttr
            val second: Option[String] = words.lift(1)
            val num: Option[Int] = second.flatMap(_.toIntOption)
            if num.isEmpty then elems.append(Text(s"??? $first $restOfLine"))
            else // legal integer and perhaps more on same line
              val ia: Attr[Int] = intAttrTypes(f).apply(num.get)
              elems.append(intAttrTypes(f).apply(num.get))
              val afterNumOnThisLine = restOfLine.stripLeading.drop(second.get.length).trim
              if afterNumOnThisLine.length > 0 then elems.append(Text(s"??? $afterNumOnThisLine"))

          case f if first.isEntType =>
            val ent: EntType = entTypes(f) 
            val relOpt: Option[RelType] = relTypes.get(words.last)
            val second: Option[String]  = words.lift(1)
            val third: Option[String]   = words.lift(2)
            val thirdRelOpt: Option[RelType] = third.flatMap(t => relTypes.get(t)) 
            if relOpt.isEmpty && second.isDefined && third.isEmpty then // single entity
              elems.append(ent.apply(second.get))
            else if relOpt.isEmpty && second.isDefined && third.isDefined then // extra after id
              elems.append(ent.apply(second.get)) 
              elems.append(Text(s"??? ${restOfLine.skipFirstToken}"))
            else 
              elems.append(Text("TODO"))

          case f =>
            val value: String = parseFollowingIndentedLines(line)
            elems.append(Text(value))

        end match
      end if

      i += 1

    end while  
    
    elems.toList 
  end parseLines