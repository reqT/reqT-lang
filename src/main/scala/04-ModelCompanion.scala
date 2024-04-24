package reqt

import scala.collection.mutable

/** Operations of companion to trait `Model` **/
transparent trait ModelCompanion:
  self: Model.type =>

  def apply(elems: Elem*): Model = Model(elems.toVector)

  val empty: Model = Model()

  def random(
    nbrTopLevelElems: Int, 
    relationRate: Double = 0.3, 
    nextLevelReduction: Int = 1, 
    attrRate: Double = 0.5,  
  ): Model =
    //TODO: also draw elem types randomly
    require(nbrTopLevelElems >= 0, "nbrTopLevelElems must be >= 0")
    require(attrRate >= 0.0 && attrRate <= 1.0, "attrRate must be in [0.0, 1.0]")
    require(relationRate >= 0.0 && relationRate <= 1.0, "relationRate must be in [0.0, 1.0]")
    require(nextLevelReduction > 0, "nextLevelReduction must be > 0")    
    import scala.util.Random.{nextDouble as rnd}
    def loop(n: Int): Model =
      val elems = Vector.fill(n){
        if rnd() < attrRate then Prio(1)
        else if rnd() < relationRate then 
          val next = n - nextLevelReduction
          val subModel = 
            if next < 1 then Model.empty else loop(next)
          if subModel.elems.isEmpty then Feature("x") else Rel(Feature("x"), Has, subModel)
        else Feature("x")
      }
      Model(elems)
    end loop
    if nbrTopLevelElems > 0 then loop(nbrTopLevelElems) else Model.empty

  extension (elems: Seq[Elem]) 
    def toModel = Model(elems.toVector)

    def concatAdjacent(sat: StrAttrType, delim: String): Vector[Elem] =
      if elems.length < 2 then elems.toVector else
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
