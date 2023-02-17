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

  /** A Model with elems deeply filtered according to a selection expression. **/
  infix def keep(s: selection.Expr): Model = selection(s, this)

  /** A sub-model of Link **/
  def /(link: Link): Model = self / Vector(link)

  /** A deep sub-model recursing into a sequence of links **/
  def /(links: Vector[Link]): Model = 
    links match
      case Vector() => Model()

      case Vector(link) => 
        val ms: Vector[Model] = link match 
          case e: Ent      => elems.collect{ case r: Rel if r.e == e && r.rt == Has => r.sub}
          case el: EntLink => elems.collect{ case r: Rel if r.e == el.e && r.rt == el.rt => r.sub}
        ms.foldLeft(Model())(_ ++ _)

      case Vector(link, rest*) => 
        val m2 = self / link
        m2 / rest.toVector


  def /[T](a: Attr[T]): Boolean = elems.exists(_ == a)

  def /[T](at: AttrType[T]): Vector[T] = elems.collect{case a: Attr[T] if a.at == at => a.value}

  def /[T](u: Undefined[T]): Vector[Undefined[T]] = elems.collect{case Undefined(at) if u.at == at => u} 

  def /(ut: Undefined.type): Vector[Undefined[?]] = elems.collect{case u: Undefined[?] => u} 

  def /[T](p: AttrTypePath[T]): Vector[T] =  self / p.links / p.dest

  def /[T](p: AttrPath[T]): Boolean =  self / p.links / p.dest