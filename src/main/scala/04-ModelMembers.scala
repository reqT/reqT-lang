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

  /** A new Model with elem e prepended to the elems of this Model. Same as `e +: m` */
  def prepend(e: Elem): Model = Model(e +: elems)

  /** A new Model with elem e prepended to the elems of this Model. Same as `m.prepend(e)` */
  def +:(e: Elem): Model = prepend(e)

  /** A new Model with elem e appended to the elems of this Model. Same as `m :+ e` */
  def append(e: Elem): Model = Model(elems :+ e)

  /** A new Model with elem e appended to the elems of this Model. Same as `m.append(e)` */
  def :+(e: Elem): Model = append(e)

  /** A new Model with other Model's elems appended to elems. Same as: `m :++ other` 
    * NOTE: Different from `m ++ other` */
  def append(other: Model): Model = Model(elems :++ other.elems)  

  def :++(other: Model): Model = append(other)

  /** A new model first attribute of same type updated to `a` or appended to elems if not in `elems.tip`. */
  def updated[T](a: Attr[T]): Model = 
    var isReplaced = false
    val es = elems.map: e => 
      e match
        case a2: Attr[?] if !isReplaced && a.t == a2.t => 
          isReplaced = true
          a  
        case elem => elem 
    Model(if isReplaced then es else elems :+ a)

  /** merge sub model of r with the sub model of first relations with the same link or append r to elems */
  def mergeFirst(r: Rel): Model = 
    var isMerged = false
    val es = elems.map: e => 
      e match
        case r2: Rel if !isMerged && r.link == r2.link => 
          isMerged = true
          Rel(r2.e, r2.t, r2.sub ++ r.sub)  // recur with add
        case elem => elem 
    Model(if isMerged then es else elems :+ r)

  /** Append an elem if not already exists at top level. Relations are merged using mergeFirst. */
  def add(e: Elem): Model = 
    e match
      case n: Node => if !elems.exists(_ == n) then Model(elems :+ n) else self
      case r: Rel  => self.mergeFirst(r)

  def +(e: Elem): Model = add(e)

  def addAll(es: Elem*): Model = 
    var result = self 
    for e <- es do result += e
    result

  def addAll(es: Vector[Elem]): Model = 
    var result = self 
    for e <- es do result += e
    result

  def addAll(other: Model): Model =  addAll(other.elems) 

  /** A new Model with each elem of other Model added to elems of this Model. Same as `m.addAll(other)` **/
  def ++(other: Model): Model = addAll(other)  
  
  def -(e: Elem): Model = Model(elems.filterNot(_ == e)) // what TODO if e is Rel?

  def -(t: ElemType): Model = Model(elems.filterNot(_.t == t)) // what TODO if e is Rel?

  def -(l: Link): Model = 
    val es = elems.filterNot: 
      case r: Rel if r.link == l => false 
      case _ => true
    Model(es)

  def nodes: Vector[Node] = elems.flatMap:
    case n: Node => Vector(n) 
    case Rel(e, r, m) => Vector(e) ++ m.nodes

  def links: Vector[Link] = elems.flatMap:
    case _: Node => Vector() 
    case Rel(e, r, m) => Vector(Link(e,r)) ++ m.links

  def relTypes: Vector[RelType] = links.map(_.t)

  def rels: Vector[Rel] = elems.flatMap:
    case _: Node => Vector() 
    case r@Rel(_, _, m) => Vector(r) ++ m.rels


  def undefined: Vector[Undefined[?]] = nodes.collect{ case u: Undefined[?] => u }

  def ents: Vector[Ent]         = nodes.collect { case e: Ent => e }
  def entTypes: Vector[EntType] = nodes.collect { case e: Ent => e.t }
  def attrs: Vector[Attr[?]]    = nodes.collect { case a: Attr[?] => a }
  def strAttrs: Vector[StrAttr] = nodes.collect { case a: StrAttr => a }
  def strValues: Vector[String] = nodes.collect { case a: StrAttr => a.value }
  def intAttrs: Vector[IntAttr] = nodes.collect { case a: IntAttr => a }
  def intValues: Vector[Int]    = nodes.collect { case a: IntAttr => a.value }
  
  lazy val ids: Vector[String] = ents.map(_.id)

  lazy val idTypeMap: Map[String, Set[EntType]] = 
    ents.groupBy(_.id).map((id, xs) => id -> xs.map(_.t).toSet)
  
  lazy val idMap: Map[String, Vector[Ent]] = 
    ents.groupBy(_.id).map((id, xs) => id -> xs.toVector)

  def isIdTypeDistinct: Boolean = idTypeMap.forall((id, xs) => xs.size == 1)

  def nonTypeDistinctIds: Map[String, Set[EntType]] = idTypeMap.filter((id, xs) => xs.size > 1)

  def entsOfId(id: String): Vector[Ent] = idMap.get(id).getOrElse(Vector())

  def getEntOfId(id: String): Ent = idMap(id).head

  def entTypesOfId(id: String): Set[EntType] = idTypeMap(id)

  def rankBy[T](iat: IntAttrType, rt: RelType = Has): Vector[Ent] = 
    val lrs: Model = leafRelsOf(iat)
    lrs.ents.sortBy(e => (lrs / Link(e, rt) / iat).headOption)

  def withRank(iat: IntAttrType, rt: RelType = Has): Vector[Rel] =
    ents.zipWithIndex.map((e, i) => Rel(e, rt, Model(iat.apply(i + 1))))

  /** A new Model with distinct elems (non-recursive). **/
  def distinctTopElems: Model = Model(elems.distinct) 

  /** A new Model that is distinct by attribute type (non-recursive). **/
  def distinctTopAttrType: Model =
    val foundAttrTypes: collection.mutable.Set[AttrType[?]] = collection.mutable.Set()
    val es = elems.flatMap:
      case a: Attr[?] => 
        if foundAttrTypes(a.t) then Vector()
        else 
          foundAttrTypes += a.t
          Vector(a)
      case e => Vector(e)
    Model(es)

  /** A new Model that removes Ent that are also part of Links. **/
  def distinctEntLinks: Model =
    val es = elems.flatMap:
      case e: Ent => 
        if elems.exists: 
          case Rel(e2, t, sub) if e == e2 => true
          case _ => false 
        then Seq()
        else Seq(e)
      case e => Seq(e)
    Model(es)

  /** A new Model with recursive de-duplication of its elems on all levels. **/
  def distinctElemsDeep: Model =
    val es = elems.distinct.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.distinctElemsDeep)
    Model(es) 

  /** A new Model with recursive de-duplication of its attributes by type on all levels.  **/
  def distinctAttrTypeDeep: Model =
    val es = distinctTopAttrType.elems.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.distinctAttrTypeDeep)
    Model(es) 

  /** Recursively sort elems alphabetically. */  
  // TODO: maybe better to give each Elem ordering and IntAttr special ordering???
  def sorted(using Ordering[Elem]): Model = 
    val es = elems.map:
      case n: Node => n 
      case Rel(e, r, m) => Rel(e, r, m.sorted)  // recur
    Model(es.sorted) // sorted is using Elem.elemOrd implicitly

  /** All empty relations at any depth are replaced by its entity. */
  def removeEmptyRelations: Model =
    Model(elems.map:
      case n: Node => n 
      case Rel(e, r, m) => if m.elems.nonEmpty then Rel(e, r, m.removeEmptyRelations) else e
    )
  
  /** A Map that groups equal links to gether. Keys of type Link point to Vector[Rel]. **/
  def groupByLink: Map[Link | Elem, Vector[Elem]] = elems.groupBy: 
      case Rel(e, rt, sub) => Link(e, rt)
      case e => e

  /** */
  def appendEqualRel: Model =  
    val ess: Iterable[Vector[Elem]] = groupByLink.map:
      case (Link(e, rt), xs) => 
        val merged: Rel = 
          xs.asInstanceOf[Vector[Rel]].reduceLeft: (r1, r2) => 
            Rel(r1.e, r1.t, (r1.sub :++ r2.sub).appendEqualRel)
        Vector(merged)
      case (e, xs) => xs

    Model(ess.flatten.toVector)

  /** A new model constructed by adding all elems using add one by one giving no duplicates. */
  def distinct = Model() ++ self  // TODO: investigate if this is redundant to distinctElemsDeep???

  /** A Model in normal form: elems are added one by one replacing same nodes and then sorted. **/
  def normalize: Model = distinct.sorted

  def prune: Model = 
    removeEmptyRelations.distinctElemsDeep.distinctAttrTypeDeep.distinctEntLinks.distinct

  def atoms: Vector[Elem] = paths.flatMap(_.toModel.elems)

  def expand: Model = Model(atoms) 

  /** True if this model is in normal form. */
  def isNormal: Boolean = self == normalize

  /** A Model with the nodes but not relations at the top of this Model. */
  def tip: Model = cut(0)
  
  /** A Model with the tip of this Model and the tip of its sub-models. */
  def top: Model = cut(1)

  def sub: Model =
    elems.collect { case Rel(e, rt, sub) => sub }.foldLeft(Model())(_ :++ _)
  
  /** Submodels with same link are merged **/
  def linkMapOf(et: EntType): Map[Link, Model] = 
    val ls: Seq[(Link, Model)]  = elems.collect { case Rel(e, rt, sub) if e.t == et => Link(e, rt) -> sub }
    val grouped: Map[Link, Seq[(Link, Model)]] = ls.groupBy(_._1)
    val merged: Map[Link, Model] = grouped.map((l, xs) => l -> xs.map(_._2).foldLeft(Model())(_ :++ _))
    merged

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
        val ms: Vector[Model] = elems.collect{ case r: Rel if r.e == link.e && r.t == link.t => r.sub}
        ms.foldLeft(Model())(_ :++ _)

      case Vector(link, rest*) => 
        val m2 = self / link
        m2 / LinkPath(rest.toVector)

  def /[T](a: Attr[T]): Boolean = elems.exists(_ == a)

  def /[T](at: AttrType[T]): Vector[T] = elems.collect{
    case a: Attr[?] if !a.isInstanceOf[Undefined[?]] && a.t == at => a.value.asInstanceOf[T]
  }

  def /(e: Ent): Boolean = elems.exists(_ == e)

  def /(et: EntType): Vector[String] = elems.collect{case e: Ent if e.t == et => e.id}

  def /[T](u: Undefined[T]): Vector[Undefined[T]] = elems.collect{case Undefined(at) if u.t == at => u} 

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

  def pathsOf[T](at: AttrType[T]): Vector[AttrPath[T]] = 
    val pb = collection.mutable.ArrayBuffer.empty[AttrPath[T]]

    def recur(links: Vector[Link], m: Model): Unit =
      for e <- m.elems do 
        e match
          case a: StrAttr if a.t == at => pb.append(AttrPath(links, a.asInstanceOf[Attr[T]]))
          case a: IntAttr if a.t == at => pb.append(AttrPath(links, a.asInstanceOf[Attr[T]]))
          case Rel(e, rt, sub) => recur(links :+ Link(e, rt), sub) 
          case _ => 
    end recur
    recur(Vector(), self)
    pb.toVector

  def bottom(n: Int): Model = paths.map(_.takeLinksRight(n)).toModel

  lazy val leafs: Model = bottom(0)

  lazy val leafRels: Model = Model(bottom(1).elems.collect{ case r: Rel => r})

  def leafRelsOf[T](at: AttrType[T]): Model = 
    Model(leafRels.elems.collect{ case r: Rel if r.sub.elems.exists(e => e.t == at) => r})

  def leafRelsOf(et: EntType): Model = 
    Model(leafRels.elems.collect{ case r: Rel if r.e.t == et => r})


  def concatAdjacentText = Model(elems.concatAdjacent(Text, "\n"))

  /** */
  def trim: Model = 
    extension (es: Vector[Elem]) def dropWhileEmptyText: Vector[Elem] = 
      es.dropWhile:
        case StrAttr(Text, s) if s.trim.isEmpty => true
        case _ => false

    // optimize this as double reverse is inefficient ...
    Model(elems.reverse.dropWhileEmptyText.reverse.dropWhileEmptyText) 

  def toMarkdown: String =
    // TODO turn tweaks below into default args or context using MarkdownSettings
    val MaxLen = 72  // Limit for creating one-liners in pretty markdown; TODO: should this be hardcoded???
    val isDetectOneLiner = true  // perhaps not allow one liner un-parsing as it is non-regular
    val isInsertColon = true

    val colonOpt = if isInsertColon then ":" else ""
    val sb = StringBuilder()

    def recur(level: Int, m: Model): Unit = 
      val indent = "  " * level 
      for e <- m.elems do e match
        case Undefined(at) => 
          sb.append(s"$indent* $at\n")

        case a: Attr[?] => 
          val trimmed = a.value.toString.trim
          val formattedValue = 
            if !trimmed.hasNewline then trimmed else "\n" + trimmed.trimIndent(level * 2 + 2)

          if a.t == Text then sb.append(s"$indent$formattedValue\n") 
          else if a.t == Title && formattedValue.startsWith("#") then 
            sb.append(s"$indent$formattedValue\n")  //never colon after title
          else sb.append(s"$indent* ${a.t}$colonOpt $formattedValue\n")
        
        case e: Ent => 
          sb.append(s"$indent* ${e.t}$colonOpt ${e.id}\n")
        
        case Rel(e, rt, Model(Vector(e2: Ent))) 
          if isDetectOneLiner && !e.id.contains('\n') && e.id.length + indent.length < MaxLen 
          => // a one-liner
            sb.append(s"$indent* ${e.t}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${e2.t}$colonOpt ${e2.id}\n")
        
        case Rel(e, rt, Model(Vector(u: Undefined[?]))) 
          if isDetectOneLiner 
          => // a one-liner
          sb.append(s"$indent* ${e.t}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${u.t}\n")
        
        case Rel(e, rt, Model(Vector(a: Attr[?]))) 
          if isDetectOneLiner && !a.value.toString.contains('\n') && a.value.toString.length + indent.length < MaxLen 
          => // a one-liner
            sb.append(s"$indent* ${e.t}$colonOpt ${e.id} ${rt.toString.deCapitalize} ${a.t}$colonOpt ${a.value}\n")
        
        case Rel(e, rt, sub) => // put sub on indented new line 
          sb.append(s"$indent* ${e.t}$colonOpt ${e.id} ${rt.toString.deCapitalize}\n")
          if sub.elems.length > 0 then recur(level + 1, sub)
    end recur

    recur(0, self)
    sb.toString
  end toMarkdown

  def showCompact: String = elems.map(_.show).mkString("Model(",",",")")
  def showLines: String = elems.map(_.show).mkString("Model(\n  ",",\n  ","\n)")

  override def toString: String = elems.mkString("Model(",",",")")
