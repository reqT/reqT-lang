package reqt

trait ModelOps:
  self: Model =>

  def ++(other: Model): Model = Model(elems ++ other.elems)
  
  def +(e: Elem): Model = Model(elems :+ e)

  def tip: Model = Model(elems.collect { case n: Node => n case Rel(e, _, _) => e }.distinct)
  
  def top: Model = Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.tip) })
