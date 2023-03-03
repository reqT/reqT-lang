package reqt

/** Operations of companion to trait `Model` **/
transparent trait ModelCompanionOps:
  self: Model.type =>

  def apply(elems: Elem*): Model = Model(elems.toVector)