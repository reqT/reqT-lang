package reqt

extension (s: String) def toModel = parser.parseModel(s)

extension (sc: StringContext)
  def m(args: Any*): Model = 
    val strings: Iterator[String] = sc.parts.iterator
    val expressions: Iterator[Any] = args.iterator
    val sb = StringBuilder(strings.next)
    while strings.hasNext do
        sb.append(expressions.next.toString)
        sb.append(strings.next)
    sb.toString.toModel

object parser:
  import meta.*
  extension (s: String)
    def toLines: Array[String] = s.split("\n")
    def toWords: Array[String] = s.split(" ").map(_.trim).filter(_.nonEmpty)

    def skipIndent: String     = s.dropWhile(ch => ch.isSpaceChar || ch == '\t')
    def skipFirstWord: String  = s.dropWhile(ch => !(ch.isSpaceChar || ch == '\t'))
    def skipFirstToken: String = s.skipIndent.skipFirstWord.trim

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
            if num.isEmpty then 
              elems.append(Err(s"??? Integer expected after $f on line $i: $first $restOfLine"))
            else // legal integer and perhaps more on same line
              val ia: Attr[Int] = intAttrTypes(f).apply(num.get)
              elems.append(intAttrTypes(f).apply(num.get))
              val afterNumOnThisLine = restOfLine.stripLeading.drop(second.get.length).trim
              if afterNumOnThisLine.length > 0 then elems.append(Err(s"??? $afterNumOnThisLine"))

          case f if first.isEntType =>
            val ent: EntType = entTypes(f) 
            val relOpt: Option[RelType] = relTypes.get(words.last)
            val second: Option[String]  = words.lift(1)
            val third: Option[String]   = words.lift(2)
            val thirdRelOpt: Option[RelType] = third.flatMap(t => relTypes.get(t)) 
            if relOpt.isEmpty && second.isDefined then 
              if third.isEmpty then 
                // single entity
                elems.append(ent.apply(second.get))
                val nextLine = if i + 1 < lines.length then lines(i + 1) else ""
                if nextLine.level > level then 
                  elems.append(Err(s"??? bad indent on line ${i + 1}, missing relation type after: $line"))
              else  
                // more after id
                elems.append(ent.apply(second.get)) 
                elems.append(Err(s"??? ${restOfLine.skipFirstToken}"))
            else if relOpt.isDefined && second.isDefined && third.isDefined then
                // single relation start
                val here = i
                val until = indexOfLastFollowingLineWithHigherIndentLevel
                val subElems = if until == i then List() else parseLines(i + 1, until + 1, lines)
                i = until 
                elems.append(Rel(ent.apply(second.get), relOpt.get, Model(subElems*)))
                if relOpt.get.toString != third.get.capitalize then
                  // Feature xxx yyy has 
                  elems.append(Err(s"??? Illegal multi-word id ${second.get} ${third.get} on line $here: $line"))
            else if third.isEmpty then 
              elems.append(Err(s"??? Missing id after $f on line $i: $line"))
            else
              elems.append(Err(s"??? Illegal relation on line $i: $line\nsecond=$second relOpt=$relOpt third=$third"))

          case f =>
            val value: String = parseFollowingIndentedLines(line)
            elems.append(Text(value))

        end match
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList 
  end parseLines