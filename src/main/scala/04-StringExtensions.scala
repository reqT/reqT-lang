package reqt

object StringExtensions:
  extension (s: String)
    def toModel = ModelParser.parseModel(s)

    def p = println(s)
    def toLines: Array[String] = s.split("\n")
    def toWords: Array[String] = s.split(" ").map(_.trim).filter(_.nonEmpty)

    def skipIndent: String     = s.dropWhile(ch => ch.isSpaceChar || ch == '\t')
    def skipFirstWord: String  = s.dropWhile(ch => !(ch.isSpaceChar || ch == '\t'))
    def skipFirstToken: String = s.skipIndent.skipFirstWord.trim
    def isElemStart: Boolean = meta.isConceptName(s.skipIndent.takeWhile(ch => !(ch.isSpaceChar || ch == '\t')))
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
