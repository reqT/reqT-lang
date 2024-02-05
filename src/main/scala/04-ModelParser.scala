package reqt

object ModelParser:
  import meta.*

  extension (s: String)
    def toModel = ModelParser.parseModel(s)
  
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
    //println(s">>> debug:\nparseLines(fromIndex=$fromIndex, untilIndex=$untilIndex, lines=$lines, baseLevel=$baseLevel)")
    val elems = scala.collection.mutable.Buffer.empty[Elem]
    var i = fromIndex
    while i < untilIndex do

      val line: String = lines(i)
      //println(s"line=$line")
      val level: Int = line.level(baseLevel)
      //println(s"level=$level")
      val words: Array[String] = line.toWords

      if words.isEmpty then elems.append(Text(""))  // Line is empty or white space only
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
          //println(s""">>> isFirstBulletElem==false first=="$first"""")
          appendUntilEnd(Text)
        else // we know it starts with <indent> *
          val secondWord = words(1)
          val restOfLine = line.removeLeadingBullet.skipFirstToken

          secondWord match
          case w if w.isStrAttrType => 
            val j = endOfTextBlock // check if more lines with just indented text
            val extra = if j > i then takeLines(j).mkString("\n","\n","") else ""
            val value: String = restOfLine ++ extra
            val sa = strAttrTypes(w).apply(value)
            elems.append(sa)

          case w if w.isIntAttrType => 
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
            val ent: EntType = entTypes(w) 
            val thirdOpt: Option[String] = words.lift(2)
            val r: Int = words.indexWhere(s => relTypes.isDefinedAt(s))

            if r == -1 then // there is no RelType given
              val wordsWithId = thirdOpt match
                case Some(id) if isConceptName(id) => // there is no id but a concept
                   w +: Ent.emptyId +: words.drop(2)  // add empty id and then rest (drop leading * Ent)
                case _ =>  // there is an id that does not start with a concept 
                  words.drop(1)  // drop leading *

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
              else elems.append(ent.apply(id))
            else // this is a relation
              //println(s""">>> debug: parsing relation""")
              val rt = relTypes(words(r))
              val idMaybeEmpty = words.slice(2, r).mkString(" ")  // 2 as we want to skip leading *
              val id = if idMaybeEmpty.isEmpty then Ent.emptyId else idMaybeEmpty
              val remainingWords = words.slice(r + 1, words.length)
              //println(s""">>> debug: remainingWords.mkString(" ")=="${remainingWords.mkString(" ")}" """)
              val extraElemsOnThisLine: List[Elem] = 
                if remainingWords.isEmpty then List.empty else 
                  //println(s">>> debug; recurse parseElems as if this was a new line starting with *")
                  parseElems(remainingWords.mkString("* ", " ", ""), level)
              val j = endOfBlock
              val extraSubsequentElems: List[Elem] = 
                if j == i then Nil else 
                  val subLines = takeLines(j)
                  //println(s">>> debug; recurse parseLines ")
                  parseLines(0, subLines.length, subLines, baseLevel)
              val rel = Rel(ent(id), rt, Model((extraElemsOnThisLine ++ extraSubsequentElems)*))
              elems.append(rel)

          case _ => appendUntilEnd(Text)// everything else is a Text attribute
          end match
        end if
      end if

      i += 1  // goto next line

    end while  
    
    elems.toList

  end parseLines
end ModelParser