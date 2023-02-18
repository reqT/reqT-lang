package reqt

trait ModelOps:
  self: Model =>

  def size: Int =
    var n = 0
    ???
    

  /** A Model with elems of other Model to elems of this Model. **/
  def ++(other: Model): Model = Model(elems ++ other.elems)

  /** A Model with elem e appended to this Model. **/  
  def +(e: Elem): Model = Model(elems :+ e)
  
  /** A Model with de-duplicated elems at all levels **/
  def distinct: Model = 
    Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.distinct) }.distinct) 

  /** A Model with the top level nodes of this Model. */
  def tip: Model = 
    Model(elems.collect { case n: Node => n case Rel(e, _, _) => e })
  
  /** A Model with the tip of this Model and the tip of its sub-models. */
  def top: Model = 
    Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.tip) })

  def sub: Model =
    elems.collect { case Rel(e, r, sub) => sub }.foldLeft(Model())(_ ++ _)

  def level(n: Int): Model = n match
    case 0 => tip
    case 1 => top
    case 2 => Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.top) })
    case 3 => Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.level(n - 1)) })

  /** A Model with elems deeply filtered according to a selection expression. **/
  infix def keep(s: selection.Expr): Model = selection(s, this)

  /** A sub-model of Link **/
  def /(link: Link): Model = self / Vector(link)

  /** A deep sub-model recursing into a sequence of links **/
  def /(links: Vector[Link]): Model = 
    links match
      case Vector() => self

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

  def txt: String = 
    def loop(level: Int, m: Model, sb: StringBuilder): Unit = 
      val indent = "  " * level 
      for e <- m.elems do e match
        case Undefined(at) => sb.append(s"$indent$at\n")
        case a: Attr[?] => sb.append(s"$indent${a.at} ${a.value}\n")
        case e: Ent     => sb.append(s"$indent${e.et} ${e.id}\n")
        case Rel(e, rt, sub) => 
          sb.append(s"$indent${e.et} ${e.id} ${rt.toString.toLowerCase}\n")
          loop(level + 1, sub, sb)
    end loop
    val sb = StringBuilder()
    loop(0, self, sb)
    sb.toString

