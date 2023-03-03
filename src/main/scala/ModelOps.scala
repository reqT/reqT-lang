package reqt

/** Operations of trait `Model` **/
transparent trait ModelOps:
  self: Model =>

  /** The number of elems at top level plus the sizes of all sub models **/
  def size: Int =
    var n = elems.size
    elems.foreach:
      case n: Node => 
      case r: Rel => n += r.sub.size
    n

  /** A new Model with elems of other Model to elems of this Model. **/
  def ++(other: Model): Model = Model(elems ++ other.elems)

  /** A new Model with elem e appended to this Model. **/  
  def +(e: Elem): Model = Model(elems :+ e)

  def nodes: Vector[Node] = 
    elems.flatMap:
      case n: Node => Vector(n) 
      case Rel(e, r, m) => Vector(e) ++ m.nodes

  def undefined: Vector[Undefined[?]] = nodes.collect{ case u: Undefined[?] => u }
  
  /** A new Model with deep de-duplication of its elems. **/
  def distinct: Model =
    Model(elems.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.distinct)
    .distinct) 

  def sorted: Model = 
    Model(elems.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.sorted)
    .sortBy(_.show)) 

  /** All empty relations at any depth are replaced by its entity. */
  def cutEmptyRelations: Model =
    Model(elems.map:
      case n: Node => n 
      case Rel(e, r, m) => if m.elems.nonEmpty then Rel(e, r, m.cutEmptyRelations) else e
    )

  /** A Model with the nodes but not relations at the top of this Model. */
  def tip: Model = cut(0)
  
  /** A Model with the tip of this Model and the tip of its sub-models. */
  def top: Model = cut(1)

  def sub: Model =
    elems.collect { case Rel(e, r, sub) => sub }.foldLeft(Model())(_ ++ _)

  /** Cut all relations so that no relations is deeper than depth. cut(0) == tip, cut(1) == top **/
  def cut(depth : Int): Model = 
    if depth <= 0 then  Model(elems.map { case n: Node => n case Rel(e, _, _) => e }) 
    else Model(elems.map { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.cut(depth - 1)) })

  /** A Model with elems deeply filtered according to a selection expression. **/
  infix def keep(s: selection.Expr): Model = selection(s, this)

  /** A sub-model of Link **/
  def /(link: Link): Model = self / LinkPath(Vector(link))

  /** A deep sub-model recursing into a sequence of links in a LinkPath. **/
  def /(p: LinkPath): Model = 
    p.links match
      case Vector() => self

      case Vector(link) => 
        val ms: Vector[Model] = link match 
          case e: Ent      => elems.collect{ case r: Rel if r.e == e && r.rt == Has => r.sub}
          case el: EntLink => elems.collect{ case r: Rel if r.e == el.e && r.rt == el.rt => r.sub}
        ms.foldLeft(Model())(_ ++ _)

      case Vector(link, rest*) => 
        val m2 = self / link
        m2 / LinkPath(rest.toVector)

  def /[T](a: Attr[T]): Boolean = elems.exists(_ == a)

  def /[T](at: AttrType[T]): Vector[T] = elems.collect{case a: Attr[T] if a.at == at => a.value}

  def /[T](u: Undefined[T]): Vector[Undefined[T]] = elems.collect{case Undefined(at) if u.at == at => u} 

  def /(ut: Undefined.type): Vector[Undefined[?]] = elems.collect{case u: Undefined[?] => u} 

  def /[T](p: AttrTypePath[T]): Vector[T] =  self / LinkPath(p.links) / p.dest

  def /[T](p: AttrPath[T]): Boolean =  self / LinkPath(p.links) / p.dest

  def txt: String = 
    val MaxLen = 72
    def loop(level: Int, m: Model, sb: StringBuilder): Unit = 
      val indent = "  " * level 
      for e <- m.elems do e match
        case Undefined(at) => sb.append(s"$indent$at\n")

        case a: Attr[?] => sb.append(s"$indent${a.at} ${a.value}\n")
        
        case e: Ent     => sb.append(s"$indent${e.et} ${e.id}\n")
        
        case Rel(e, rt, Model(Vector(e2: Ent))) 
          if !e.id.contains('\n') && e.id.length + indent.length < MaxLen => // a one-liner
          sb.append(s"$indent${e.et} ${e.id} ${rt.toString.toLowerCase} ${e2.et} ${e2.id}\n")
        
        case Rel(e, rt, Model(Vector(u: Undefined[?]))) => // a one-liner
          sb.append(s"$indent${e.et} ${e.id} ${rt.toString.toLowerCase} ${u.at}\n")
        
        case Rel(e, rt, Model(Vector(a: Attr[?]))) 
          if !a.value.toString.contains('\n') && a.value.toString.length + indent.length < MaxLen => // a one-liner
          sb.append(s"$indent${e.et} ${e.id} ${rt.toString.toLowerCase} ${a.at} ${a.value}\n")
        
        case Rel(e, rt, sub) => // put sub on indented new line 
          sb.append(s"$indent${e.et} ${e.id} ${rt.toString.toLowerCase}\n")
          if sub.elems.length > 0 then loop(level + 1, sub, sb)
    end loop
    val sb = StringBuilder()
    loop(0, self, sb)
    sb.toString

  def p: Unit = println(txt)

  override def toString: String = elems.mkString("Model(",",",")")
