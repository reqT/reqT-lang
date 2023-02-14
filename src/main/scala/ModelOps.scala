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
  def keep(selection: Selection): Model =
    val terms = selection match 
      case se: SelectionExpr => se.terms.toSet
      case st: SelectionTerm => Set(st)

    val pickedElems = elems.flatMap:
      elem => elem match
        case e: Ent     if terms.contains(e.et) || terms.contains(e) => Some(e) 

        case a: Attr[?] if terms.contains(a.at) || terms.contains(a) => Some(a)

        case r: Rel => 
          val sub = r.sub.keep(selection)
          if sub.elems.nonEmpty then Some(Rel(r.e, r.rt, sub)) 
          else if 
            terms.contains(r.rt) ||  
            terms.contains(r.e & r.rt) || 
            terms.contains(r.e.et & r.rt) ||
            terms.contains(r) ||
            r.expandSubnodes.exists(rel => terms.contains(rel))
          then Some(Rel(r.e, r.rt, sub))
          else None

        case _ => None
    
    Model(pickedElems)
