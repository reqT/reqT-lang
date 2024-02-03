package reqt

object ModelParser:
  import meta.*
  import StringExtensions.*
  
  extension (sc: StringContext)
    def m(args: Any*): Model = 
      val strings: Iterator[String] = sc.parts.iterator
      val expressions: Iterator[Any] = args.iterator
      val sb = StringBuilder(strings.next)
      while strings.hasNext do
          sb.append(expressions.next.toString)
          sb.append(strings.next)
      parseModel(sb.toString)

  def parseModel(input: String): Model = Model(parseElems(input, 0)*)

  def parseElems(input: String, baseLevel: Int): List[Elem] = 
    val lines: Array[String] = input.toLines
    parseLines(0, lines.length, lines, baseLevel)

  def parseLines(fromIndex: Int, untilIndex: Int, lines: Array[String], baseLevel: Int): List[Elem] = 
    //println(s"parseLines(fromIndex=$fromIndex, untilIndex=$untilIndex, lines=$lines, baseLevel=$baseLevel)")
    val elems = List.empty[Elem].toBuffer
    var i = fromIndex
    while i < untilIndex do

      val line: String = lines(i)
      //println(s"line=$line")
      val level: Int = line.level(baseLevel)
      //println(s"level=$level")
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

        first match
          case f if first.isStrAttrType => 
            val j = endOfTextBlock // check if more lines with just indented text
            val extra = if j > i then takeLines(j).mkString("\n","\n","") else ""
            val value: String = restOfLine ++ extra
            val sa = strAttrTypes(f).apply(value)
            elems.append(sa)

            
          case f if first.isIntAttrType => 
            val second: Option[String] = words.lift(1)
            val numOpt: Option[Int] = second.flatMap(_.toIntOption)
            val ia: Attr[Int] = if numOpt.isDefined then intAttrTypes(f).apply(numOpt.get) else Undefined(intAttrTypes(f))
            elems.append(ia)
            val afterNumOnThisLine = 
              restOfLine.stripLeading.drop(if numOpt.isDefined && second.isDefined then second.get.length else 0).trim
            if afterNumOnThisLine.length > 0 then 
              elems.appendAll(parseElems(afterNumOnThisLine, level))

          case f if first.isEntType =>
            val ent: EntType = entTypes(f) 
            val r: Int = words.indexWhere(w => relTypes.isDefinedAt(w))

            if r == -1 then // there is no RelType given
              val wordsWithId = words.lift(1) match
                case Some(id) if isConceptName(id) => words(0) +: Ent.emptyId +: words.drop(1)
                case _ => words  

              val remainingWords = wordsWithId.drop(2)
              val extraElemsOnThisLine: List[Elem] = parseElems(remainingWords.mkString(" "), level)
              val idStart = wordsWithId.lift(1).getOrElse(Ent.emptyId) 

              val extraIfText: String = extraElemsOnThisLine match
                case List(StrAttr(Text,s)) => s.toString
                case _ => "" 
              
              val idExtra = 
                if extraElemsOnThisLine.isEmpty then remainingWords.mkString(" ") 
                else extraIfText

              val id = s"$idStart${if idExtra.nonEmpty then " " else ""}$idExtra"

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

            else // this is a relation
              val rt = relTypes(words(r))
              val idMaybeEmpty = words.slice(1, r).mkString(" ")
              val id = if idMaybeEmpty.isEmpty then Ent.emptyId else idMaybeEmpty
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
            val value = if j > i then takeLines(endOfTextBlock).mkString(s"$line\n","\n","") else line
            elems.append(Text(value))

        end match
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList

  end parseLines
end ModelParser