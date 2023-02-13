package reqt

object parser:
  import meta.*, model.*
  extension (s: String)
    def toModel = parser.parseModel(s)
    def toLines: Array[String] = s.split("\n")
    def toWords: Array[String] = s.split(" ").map(_.trim).filter(_.nonEmpty)

    def skipIndent: String     = s.dropWhile(ch => ch.isSpaceChar || ch == '\t')
    def skipFirstWord: String  = s.dropWhile(ch => !(ch.isSpaceChar || ch == '\t'))
    def skipFirstToken: String = s.skipIndent.skipFirstWord.trim
    def isElemStart: Boolean = isConceptName(s.skipIndent.takeWhile(ch => !(ch.isSpaceChar || ch == '\t')))
    def isTextLine: Boolean = !isElemStart

    def level: Int = 
      val initSpace = s.takeWhile(ch => ch.isSpaceChar || ch == '\t')
      initSpace.replace("\\t", "  ").length

    def wrapLongLineAtWords(n: Int = 72): String = 
      val words = s.split(" ").iterator
      val sb = StringBuilder()
      var i = 0
      while words.hasNext do
        val w: String = words.next
        i += w.length
        if i > n then 
          if w.length > n then 
            sb.append(w)
            sb.append('\n')
            i = 0
          else
            sb.append('\n')
            sb.append(w)
            i = w.length 
        else sb.append(w)
        if words.hasNext then sb.append(' ')
      end while
      sb.toString

    def wrap(n: Int = 72): String = s.split("\n").map(_.wrapLongLineAtWords(n)).mkString("\n")
  end extension

  extension (xs: Array[String]) def toCamelCase: String = 
    xs.headOption.getOrElse("") ++ xs.drop(1).map(_.capitalize).mkString

  def parseModel(input: String): Model = 
    val lines: Array[String] = input.toLines
    Model(parseLines(0, lines.length, lines)*)

  def parseLines(fromIndex: Int, untilIndex: Int, lines: Array[String]): List[Elem] = 
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

        inline def endOfTextBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level > level) 
            && lines(more + 1).isTextLine 
          do more += 1
          more

        inline def endOfBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level > level) 
          do more += 1
          more

        inline def takeLines(toIndex: Int): Array[String] =
          if toIndex > i then 
            val result = lines.slice(i + 1, toIndex + 1) 
            i += toIndex
            result
          else Array()

        def parseStrAttr(sat: StrAttrType, remainingLine: String): Unit =
          val j = endOfTextBlock // check if more lines follow with just indented text
          val extra = if j > i then takeLines(j).mkString("\n","\n","") else ""
          val value: String = remainingLine ++ extra
          val sa = sat.apply(value)
          elems.append(sa)

        first match
          case f if first.isStrAttrType => parseStrAttr(strAttrTypes(f), restOfLine)
            
          case f if first.isIntAttrType => // parse IntAttr
            val second: Option[String] = words.lift(1)
            val num: Int = second.flatMap(_.toIntOption).getOrElse(0)
            val ia: Attr[Int] = intAttrTypes(f).apply(num)
            elems.append(intAttrTypes(f).apply(num))
            val afterNumOnThisLine = restOfLine.stripLeading.drop(second.get.length).trim
            if afterNumOnThisLine.length > 0 then elems.append(Text(afterNumOnThisLine))

          case f if first.isEntType =>
            val ent: EntType = entTypes(f) 
            val relIx: Int = words.indexWhere(w => relTypes.isDefinedAt(w.capitalize))
            if relIx == 0 then // single entity
              elems.append(ent.apply(words.drop(1).toCamelCase))
            else // relation
              ???

            // val relOpt: Option[RelType] = relTypes.get(words.last)
            // val second: Option[String]  = words.lift(1)
            // val third: Option[String]   = words.lift(2)
            // if relOpt.isEmpty && second.isDefined then 
            //   if third.isEmpty then 
            //     // single entity
            //     elems.append(ent.apply(second.get))
            //   else  
            //     // more after id
            //     elems.append(ent.apply(second.get)) 
            //     elems.append(Text(restOfLine.skipFirstToken)) // TODO: check if more elems on restOfLine???
            // else if relOpt.isDefined && second.isDefined && third.isDefined then
            //     // single relation start
            //     val here = i
            //     val until = endOfBlock
            //     val subElems = if until == i then List() else parseLines(i + 1, until + 1, lines)
            //     i = until 

            //     elems.append(Rel(ent.apply(second.get), relOpt.get, Model(subElems*)))
            //     if relOpt.get.toString != third.get.capitalize then
            //       // Feature xxx yyy has 
            //       elems.append(Err(s"??? Illegal multi-word id ${second.get} ${third.get} on line $here: $line"))
            // else
            //   elems.append(Err(s"??? Illegal relation on line $i: $line\nsecond=$second relOpt=$relOpt third=$third"))

          case f =>
            val value: String = s"$line\n${takeLines(endOfTextBlock).mkString("\n")}"
            elems.append(Text(value))

        end match
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList

  end parseLines
end parser