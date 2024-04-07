package reqt

object StringExtensions:
  extension (s: String)
    def p: Unit = println(s)

    def toLines: Array[String] = s.split("\n")
    def toWords: Array[String] = s.split(" ").map(_.trim).filter(_.nonEmpty)

    def firstWord: String = s.takeWhile(_.isLetter)

    def deCapitalize: String = s.take(1).toLowerCase ++ s.drop(1)

    def skipIndent: String     = s.dropWhile(ch => ch.isSpaceChar || ch == '\t')
    def skipFirstWord: String  = s.dropWhile(ch => !(ch.isSpaceChar || ch == '\t'))
    def skipFirstToken: String = s.skipIndent.skipFirstWord.trim

    def level(base: Int): Int = 
      val initSpace = s.takeWhile(ch => ch.isSpaceChar || ch == '\t')
      initSpace.replace("\\t", "  ").length + base

    def wrapLongLineAtWords(n: Int): String = 
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

    def wrap(n: Int): String = s.split("\n").map(_.wrapLongLineAtWords(n)).mkString("\n")

    def editDistanceTo(t: String): Int = 
      //https://github.com/scala/scala/blob/4e03eb5a1c7dc2cb5274a453dbff38fef12f12f4/src/compiler/scala/tools/nsc/util/EditDistance.scala#L26
      val insertCost: Int = 1
      val deleteCost: Int = 1
      val subCost: Int = 1
      val matchCost: Int = 0
      val caseCost: Int = 1
      val transpositions: Boolean = false
      val n = s.length
      val m = t.length
      if (n == 0) return m
      if (m == 0) return n

      val d = Array.ofDim[Int](n + 1, m + 1)
      0 to n foreach (x => d(x)(0) = x)
      0 to m foreach (x => d(0)(x) = x)

      for 
        i <- 1 to n
        s_i = s(i - 1)
        j <- 1 to m
      do
        val t_j = t(j - 1)
        val cost = 
          if s_i == t_j then matchCost 
          else if s_i.toLower == t_j.toLower then caseCost 
          else subCost

        val c1 = d(i - 1)(j) + deleteCost
        val c2 = d(i)(j - 1) + insertCost
        val c3 = d(i - 1)(j - 1) + cost

        d(i)(j) = c1 min c2 min c3

        if transpositions then
          if i > 1 && j > 1 && s(i - 1) == t(j - 2) && s(i - 2) == t(j - 1) then
            d(i)(j) = d(i)(j) min (d(i - 2)(j - 2) + cost)
        end if
        
      end for

      d(n)(m)
    end editDistanceTo

  end extension
