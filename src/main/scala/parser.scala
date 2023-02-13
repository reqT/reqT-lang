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

    def level(base: Int): Int = 
      val initSpace = s.takeWhile(ch => ch.isSpaceChar || ch == '\t')
      initSpace.replace("\\t", "  ").length + base

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

  def parseModel(input: String): Model = Model(parseElems(input, 0)*)

  def parseElems(input: String, baseLevel: Int): List[Elem] = 
    val lines: Array[String] = input.toLines
    parseLines(0, lines.length, lines, baseLevel)

  def parseLines(fromIndex: Int, untilIndex: Int, lines: Array[String], baseLevel: Int): List[Elem] = 
    println(s"parseLines(fromIndex=$fromIndex, untilIndex=$untilIndex, lines=$lines, baseLevel=$baseLevel)")
    val elems = List.empty[Elem].toBuffer
    var i = fromIndex
    while i < untilIndex do

      val line: String = lines(i)
      println(s"line=$line")
      val level: Int = line.level(baseLevel)
      println(s"level=$level")
      val words: Array[String] = line.toWords

      if words.nonEmpty then

        val first = words.head
        val restOfLine = line.skipFirstToken

        inline def endOfTextBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level(baseLevel) > level) 
            && lines(more + 1).isTextLine 
          do more += 1
          more

        inline def endOfBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level(baseLevel) > level) 
          do more += 1
          more

        inline def takeLines(toIndex: Int): Array[String] =
          if toIndex > i then 
            val result = lines.slice(i + 1, toIndex + 1) 
            i = toIndex
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
            val afterNumOnThisLine = restOfLine.stripLeading.drop(second.getOrElse("").length).trim
            if afterNumOnThisLine.length > 0 then elems.appendAll(parseElems(afterNumOnThisLine, level))

          case f if first.isEntType =>
            val ent: EntType = entTypes(f) 
            val r: Int = words.indexWhere(w => relTypes.isDefinedAt(w))

            if r == -1 then // ent id
              val wordsWithId = words.lift(1) match
                case Some(id) if isConceptName(id) => words(0) +: "???" +: words.drop(1)
                case _ => words  

              val remainingWords = wordsWithId.drop(2)
              val extraElemsOnThisLine: List[Elem] = parseElems(remainingWords.mkString(" "), level)
              val idStart = wordsWithId.lift(1).getOrElse("???") 

              val extraIfText: String = extraElemsOnThisLine match
                case List(Attr(Text,s)) => s
                case _ => "" 
              
              val idExtra = 
                if extraElemsOnThisLine.isEmpty then remainingWords.toCamelCase 
                else extraIfText

              val id = idStart ++ idExtra

              val extraElemsInRel = if idExtra == "" then extraElemsOnThisLine else Nil

              val j = endOfBlock
              val extraSubsequentElems: List[Elem] = 
                if j == i then Nil else 
                  val subLines = takeLines(j)
                  parseLines(0, subLines.length, subLines, baseLevel)

              val elemsToAddInRel = extraSubsequentElems ++ extraElemsInRel

              if elemsToAddInRel.nonEmpty then 
                val rel = Rel(ent(id), Has, Model(elemsToAddInRel*))
                elems.append(rel)
              else 
                elems.append(ent.apply(id))

            else // relation
              val rt = relTypes(words(r))
              val idMaybeEmpty = words.slice(1, r).toCamelCase
              val id = if idMaybeEmpty.isEmpty then "???" else idMaybeEmpty
              val remainingWords = words.slice(r + 1, words.length)
              val extraElemsOnThisLine: List[Elem] = parseElems(remainingWords.mkString(" "), level)
              val j = endOfBlock
              val extraSubsequentElems: List[Elem] = 
                if j == i then Nil else 
                  val subLines = takeLines(j)
                  parseLines(0, subLines.length, subLines, baseLevel)
              val rel = Rel(ent(id), rt, Model((extraElemsOnThisLine ++ extraSubsequentElems)*))
              elems.append(rel)

          case f => // everything else is a Text attribute
            val j = endOfTextBlock
            val value = if j > i then takeLines(endOfTextBlock).mkString(line,"\n","") else line
            elems.append(Text(value))

        end match
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList

  end parseLines
end parser