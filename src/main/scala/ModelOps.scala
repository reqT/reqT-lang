package reqt

trait ModelOps:
  self: Model =>

  /** A Model with elems of other Model to elems of this Model. **/
  def ++(other: Model): Model = Model(elems ++ other.elems)

  /** A Model with elem e appended to this Model. **/  
  def +(e: Elem): Model = Model(elems :+ e)
  
  /** A Model with the top level nodes of this Model. */
  def tip: Model = 
    Model(elems.collect { case n: Node => n case Rel(e, _, _) => e }.distinct)
  
  /** A Model with the tip of this Model and the tip of its sub-models. */
  def top: Model = 
    Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.tip) })

  /** A Model with elems picked according to a selection. **/
  def keep(s: selection.Selection): Model = selection(s, this)
