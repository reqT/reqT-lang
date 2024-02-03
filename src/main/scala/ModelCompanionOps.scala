package reqt

/** Operations of companion to trait `Model` **/
transparent trait ModelCompanionOps:
  self: Model.type =>

  def apply(elems: Elem*): Model = Model(elems.toVector)

  extension (elems: Vector[Elem]) 
    def toModel = Model(elems)