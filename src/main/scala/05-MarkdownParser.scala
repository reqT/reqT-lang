package reqt

/** A parser from Markdown-valid bullet lists with reqt.Elem. Unknown stuff is kept in Text attributes. **/
object MarkdownParser:
  import meta.*
  private inline val isDebugging = false
  private inline def debugMsg(inline s: String): Unit = inline if isDebugging then println(s">>> debugMsg: $s")

  extension (s: String)
    def toModel = MarkdownParser.parseModel(s)
  
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
    debugMsg(s"  called parseElems; baseLevel=$baseLevel, first ten chars of input=${input.take(10)}")
    val lines: Array[String] = input.toLines
    parseLines(0, lines.length, lines, baseLevel)

  def parseLines(fromIndex: Int, untilIndex: Int, lines: Array[String], baseLevel: Int): List[Elem] = 
    debugMsg(s"  parseLines(fromIndex=$fromIndex, untilIndex=$untilIndex, lines.length=${lines.length}, baseLevel=$baseLevel)")
    val elems = scala.collection.mutable.Buffer.empty[Elem]
    var i = fromIndex
    while i < untilIndex do
      val line: String = lines(i)
      debugMsg(s"""  i=$i\n  line=line(i)="$line" """)
      val level: Int = line.level(baseLevel)
      debugMsg(s"  level=$level")
      val words: Array[String] = line.toWords

      if words.isEmpty then 
        debugMsg(s"""words is empty, line="$line" """)
        elems.append(Text(""))
      else
        val first = words.head
        val isFirstHeading = first.startsWith("#") 
        val isFirstBulletElem = first == "*" && words.length > 1
        
        extension (s: String)
          def removeLeadingBullet = s.replaceFirst("\\*\\s+","")

        def endOfTextBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level(baseLevel) > level) 
            && !(lines(more + 1).trim.startsWith("*")) 
          do more += 1
          more

        def endOfBlock: Int =
          var more = i
          while more + 1 < lines.length 
            && (lines(more + 1).level(baseLevel) > level) 
          do more += 1
          more

        def takeLines(toIndex: Int): Array[String] =
          if toIndex > i then 
            val result = lines.slice(i + 1, toIndex + 1) 
            i = toIndex
            result
          else Array()

        def appendUntilEnd(sa: StrAttrType): Unit =
          val j = endOfTextBlock
          val value = if j > i then takeLines(endOfTextBlock).mkString(s"$line\n","\n","") else line
          elems.append(sa.apply(value.removeLeadingBullet))

        if isFirstHeading then appendUntilEnd(Heading)
        else if !isFirstBulletElem then 
          debugMsg(s"""isFirstBulletElem==false first=="$first"""")
          appendUntilEnd(Text)
        else 
          debugMsg("""we know the line starts with "*" perhaps after some indentation""")
          val secondWord = words(1)
          val restOfLine = line.removeLeadingBullet.skipFirstToken

          secondWord match
          case w if w.isStrAttrType => 
            debugMsg("case w if w.isStrAttrType => MATCHED")
            val j = endOfTextBlock // check if more lines with just indented text
            val extra = if j > i then takeLines(j).mkString("\n","\n","") else ""
            val value: String = restOfLine ++ extra
            val sa = strAttrTypes(w).apply(value)
            elems.append(sa)

          case w if w.isIntAttrType => 
            debugMsg("case w if w.isIntAttrType => MATCHED")
            val thirdOpt: Option[String] = words.lift(2)
            val numOpt: Option[Int] = thirdOpt.flatMap(_.toIntOption)
            val ia: Attr[Int] = 
              if numOpt.isDefined then intAttrTypes(w).apply(numOpt.get) 
              else Undefined(intAttrTypes(w))
            
            elems.append(ia)
            
            val afterNumOnThisLine = 
              restOfLine
                .stripLeading
                .drop(if numOpt.isDefined && thirdOpt.isDefined then thirdOpt.get.length else 0)
                .trim
            
            if afterNumOnThisLine.length > 0 then elems.appendAll(parseElems(afterNumOnThisLine, level))

          case w if w.isEntType =>
            debugMsg("case w if w.isEntType => MATCHED")
            val ent: EntType = entTypes(w) 
            val thirdOpt: Option[String] = words.lift(2)
            val r: Int = words.indexWhere(s => relTypes.isDefinedAt(s))

            if r == -1 then 
              debugMsg(s"  there is no RelType given on this line")

              val wordsWithId = thirdOpt match
                case Some(id) if isConceptName(id) => // there is no id but a concept
                   w +: Ent.emptyId +: words.drop(2)  // add empty id and then rest (drop leading * Ent)
                case _ =>  // there is an id that does not start with a concept 
                  words.drop(1)  // drop leading *

              val remainingWords = wordsWithId.drop(2)

              debugMsg(s"  wordsWithId==${wordsWithId.mkString("Array(",",", ")")}")
              debugMsg(s"  remainingWords==${remainingWords.mkString("Array(",",", ")")} ")

              val extraElemsOnThisLine: List[Elem] = 
                if remainingWords.nonEmpty then parseElems(remainingWords.mkString(" "), level) else Nil

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
              debugMsg(s"  j = endOfBlock = $j")

              val extraSubsequentElems: List[Elem] = 
                if j == i then Nil else 
                  val subLines = takeLines(j)
                  parseLines(0, subLines.length, subLines, baseLevel)

              val elemsToAddInRel = extraSubsequentElems ++ extraElemsInRel

              if elemsToAddInRel.nonEmpty then 
                val rel = Rel(ent(id), Has, Model(elemsToAddInRel*))
                elems.append(rel)
              else elems.append(ent.apply(id))
            else // this is a relation
              debugMsg(s"""   parsing relation""")
              val rt = relTypes(words(r))
              val idMaybeEmpty = words.slice(2, r).mkString(" ")  // 2 as we want to skip leading *
              val id = if idMaybeEmpty.isEmpty then Ent.emptyId else idMaybeEmpty
              val remainingWords = words.slice(r + 1, words.length)
              debugMsg(s"""      remainingWords.mkString(" ")=="${remainingWords.mkString(" ")}" """)
              val extraElemsOnThisLine: List[Elem] = 
                if remainingWords.isEmpty then List.empty else 
                  debugMsg(s"      recurse into parseElems as if this was a new line starting with '*'")
                  parseElems(remainingWords.mkString("* ", " ", ""), level)
              val j = endOfBlock
              val extraSubsequentElems: List[Elem] = 
                if j == i then Nil else 
                  val subLines = takeLines(j)
                  debugMsg(s"  j=$j i=$i recurse into parseLines with sublines=$subLines")
                  parseLines(0, subLines.length, subLines, baseLevel)
              val rel = Rel(ent(id), rt, Model((extraElemsOnThisLine ++ extraSubsequentElems)*))
              elems.append(rel)

          case _ => 
            debugMsg("case _ => matched")
            appendUntilEnd(Text)// everything else is a Text attribute
          end match
        end if
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList

  end parseLines
end MarkdownParser