package reqt

import scala.collection.mutable

/** Operations of companion to trait `Model` **/
transparent trait ModelCompanionOps:
  self: Model.type =>

  def apply(elems: Elem*): Model = Model(elems.toVector)

  extension (elems: Vector[Elem]) 
    def toModel = Model(elems)

    def mergeAdjacentStrAttr(sat: StrAttrType, delim: String): Vector[Elem] =
      if elems.length < 2 then elems else
        val result = scala.collection.mutable.Buffer.empty[Elem]
        var currentIndex = 0
        inline def stringOf(elem: Elem): String = elem match
          case sa@StrAttr(at, value) if at == sat => value
          case _ => null  // ugly optimization to avoid allocation

        while currentIndex < elems.length do
          val current: Elem = elems(currentIndex)
          val currentStr: String = stringOf(current)
          if currentStr == null then 
            result.append(current)
            currentIndex += 1
          else
            currentIndex += 1
            var continue: Boolean = currentIndex < elems.length
            if !continue then result.append(current) else
              val sb = mutable.StringBuilder(currentStr)
              while continue do
                val nextStr = stringOf(elems(currentIndex))
                if nextStr == null then
                  continue = false
                else 
                  sb.append(s"$delim$nextStr")
                  currentIndex += 1
                  continue = currentIndex < elems.length
              end while
              result.append(sat.apply(sb.toString))
            end if
        end while
        result.toVector
