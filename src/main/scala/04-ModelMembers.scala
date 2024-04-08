package reqt

/** Operations of trait `Model` **/
transparent trait ModelMembers:
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
  //TODO make same non-operator methods with good name like append for :+ , appendIfNotExists for + and make deep???

  def -(e: Elem): Model = Model(elems.filterNot(_ == e))

  def nodes: Vector[Node] = elems.flatMap:
    case n: Node => Vector(n) 
    case Rel(e, r, m) => Vector(e) ++ m.nodes

  def links: Vector[Link] = elems.flatMap:
    case _: Node => Vector() 
    case Rel(e, r, m) => Vector(Link(e,r)) ++ m.links

  def relTypes: Vector[RelType] = links.map(_.rt)

  def undefined: Vector[Undefined[?]] = nodes.collect{ case u: Undefined[?] => u }

  def ents: Vector[Ent]         = nodes.collect { case e: Ent => e }
  def entTypes: Vector[EntType] = nodes.collect { case e: Ent => e.et }
  def attrs: Vector[Attr[?]]    = nodes.collect { case a: Attr[?] => a }
  def strAttrs: Vector[StrAttr] = nodes.collect { case a: StrAttr => a }
  def strValues: Vector[String] = nodes.collect { case a: StrAttr => a.value }
  def intAttrs: Vector[IntAttr] = nodes.collect { case a: IntAttr => a }
  def intValues: Vector[Int]    = nodes.collect { case a: IntAttr => a.value }
  
  def ids: Vector[String] = ents.map(_.id)

  /** A new Model with distinct top-level elems. **/
  def distinctTop: Model = Model(elems.distinct) 

  /** A new Model that is distinct by top-level attribute type. **/
  def distinctAttrTop: Model = 
    val es = elems.distinctBy:
      case a: Attr[?] => a.at 
      case a => a
    Model(es)

  /** A new Model with deep de-duplication of its elems per level. **/
  def distinctDeep: Model =
    val es = elems.distinct.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.distinctDeep)
    Model(es) 

  /** A new Model with deep de-duplication of its attribute types per level. **/
  def distinctAttrDeep: Model =
    val es = distinctAttrTop.elems.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.distinctAttrDeep)
    Model(es) 

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
  
  /** A Map that groups equal links to gether. Keys of type Link point to Vector[Rel]. **/
  def groupByLink: Map[Link | Elem, Vector[Elem]] = elems.groupBy: 
      case Rel(e, rt, sub) => Link(e, rt)
      case e => e

  def mergeEqualRel: Model = 
    val ess: Iterable[Vector[Elem]] = groupByLink.map:
      case (Link(e, rt), xs) => 
        val merged: Rel = 
          xs.asInstanceOf[Vector[Rel]].reduceLeft: (r1, r2) => 
            Rel(r1.e, r1.rt, (r1.sub ++ r2.sub).mergeEqualRel)
        Vector(merged)
      case (e, xs) => xs

    Model(ess.flatten.toVector)

  /** A Model in normal form: no empty relations, distinct, sorted elems, merged equal links. **/
  def normal: Model = mergeEqualRel.cutEmptyRelations.distinctDeep.sorted
  
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
  infix def keep(s: Selection.Expr): Model = Selection.select(s, this)

  /** A sub-model of Link **/
  def /(link: Link): Model = self / LinkPath(Vector(link))

  /** A deep sub-model recursing into a sequence of links in a LinkPath. **/
  def /(p: LinkPath): Model = 
    p.links match
      case Vector() => self

      case Vector(link) => 
        val ms: Vector[Model] = elems.collect{ case r: Rel if r.e == link.e && r.rt == link.rt => r.sub}
        ms.foldLeft(Model())(_ ++ _)

      case Vector(link, rest*) => 
        val m2 = self / link
        m2 / LinkPath(rest.toVector)

  def /[T](a: Attr[T]): Boolean = elems.exists(_ == a)

  def /[T](at: AttrType[T]): Vector[T] = elems.collect{
    case a: Attr[?] if !a.isInstanceOf[Undefined[?]] && a.at == at => a.value.asInstanceOf[T]
  }

  def /(e: Ent): Boolean = elems.exists(_ == e)

  def /(et: EntType): Vector[String] = elems.collect{case e: Ent if e.et == et => e.id}

  def /[T](u: Undefined[T]): Vector[Undefined[T]] = elems.collect{case Undefined(at) if u.at == at => u} 

  def /(ut: Undefined.type): Vector[Undefined[?]] = elems.collect{case u: Undefined[?] => u} 

  def /[T](p: AttrTypePath[T]): Vector[T] =  self / LinkPath(p.links) / p.dest

  def /[T](p: AttrPath[T]): Boolean =  self / LinkPath(p.links) / p.dest

  def /[T](p: EntPath): Boolean =  self / LinkPath(p.links) / p.dest

  def /[T](p: EntTypePath): Vector[String] =  self / LinkPath(p.links) / p.dest


  def paths: Vector[Path] = 
    val pb = collection.mutable.ArrayBuffer.empty[Path]

    def recur(links: Vector[Link], m: Model): Unit =
      for e <- m.elems do e match
        case a: Attr[?] => pb.append(AttrPath(links, a))
        case e: Ent => pb.append(EntPath(links, e))
        case Rel(e, rt, sub) => recur(links :+ Link(e, rt), sub) 
    end recur
    recur(Vector(), self)
    pb.toVector

  def concatAdjacentText = Model(elems.concatAdjacent(Text, "\n"))

  def toMarkdown: String =
    // TODO turn tweaks below into default args or context using MarkdownSettings
    val MaxLen = 72  // Limit for creating one-liners in pretty markdown; TODO: should this be hardcoded???
    val isDetectOneLiner = false  // perhaps not allow one liner un-parsing as it is non-regular
    val isInsertColon = true

    val colonOpt = if isInsertColon then ":" else ""
    val sb = StringBuilder()

    def recur(level: Int, m: Model): Unit = 
      val indent = "  " * level 
      for e <- m.elems do e match
        case Undefined(at) => 
          sb.append(s"$indent* $at\n")

        case a: Attr[?] => 
          if a.at == Text then sb.append(s"$indent${a.value}\n") 
          else if a.at == Title && a.value.toString.trim.startsWith("#") then 
            sb.append(s"$indent${a.value.toString.trim}\n")  //never colon after title
          else sb.append(s"$indent* ${a.at}$colonOpt ${a.value}\n")
        
        case e: Ent => 
          sb.append(s"$indent* ${e.et}$colonOpt ${e.id}\n")
        
        case Rel(e, rt, Model(Vector(e2: Ent))) 
          if isDetectOneLiner && !e.id.contains('\n') && e.id.length + indent.length < MaxLen 
          => // a one-liner
            sb.append(s"$indent* ${e.et}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${e2.et}$colonOpt ${e2.id}\n")
        
        case Rel(e, rt, Model(Vector(u: Undefined[?]))) 
          if isDetectOneLiner 
          => // a one-liner
          sb.append(s"$indent* ${e.et}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${u.at}\n")
        
        case Rel(e, rt, Model(Vector(a: Attr[?]))) 
          if isDetectOneLiner && !a.value.toString.contains('\n') && a.value.toString.length + indent.length < MaxLen 
          => // a one-liner
            sb.append(s"$indent* ${e.et}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${a.at}$colonOpt ${a.value}\n")
        
        case Rel(e, rt, sub) => // put sub on indented new line 
          sb.append(s"$indent* ${e.et}$colonOpt ${e.id} ${rt.toString.deCapitalize}\n")
          if sub.elems.length > 0 then recur(level + 1, sub)
    end recur

    recur(0, self)
    sb.toString
  end toMarkdown

  def showCompact: String = elems.map(_.show).mkString("Model(",",",")")
  def showLines: String = elems.map(_.show).mkString("Model(\n  ",",\n  ","\n)")

  override def toString: String = elems.mkString("Model(",",",")")
