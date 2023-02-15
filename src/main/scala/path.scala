package reqt

object path:
  type Link = Ent | EntLink 

  extension (l1: Link) 
    def /(l2: Link): Vector[Link] = Vector(l1, l2)
    def /(a: Attr[String]): StrAttrPath = StrAttrPath(Vector(l1), a)
    def /(a: Attr[Int]):    IntAttrPath = IntAttrPath(Vector(l1), a)
    def /(a: StrAttrType): StrAttrTypePath = StrAttrTypePath(Vector(l1), a)
    def /(a: IntAttrType): IntAttrTypePath = IntAttrTypePath(Vector(l1), a)

  extension (ls: Vector[Link]) 
    def /(l: Link): Vector[Link] = ls :+ l
    def /(a: Attr[String]): StrAttrPath = StrAttrPath(ls, a)
    def /(a: Attr[Int]):    IntAttrPath = IntAttrPath(ls, a)
    def /(a: StrAttrType): StrAttrTypePath = StrAttrTypePath(ls, a)
    def /(a: IntAttrType): IntAttrTypePath = IntAttrTypePath(ls, a)

  sealed trait AttrTypePath[T]:
    def links: Vector[Link]
    def dest: T
  
  case class StrAttrTypePath(links: Vector[Link], dest: StrAttrType) extends AttrTypePath[StrAttrType]
  case class IntAttrTypePath(links: Vector[Link], dest: IntAttrType) extends AttrTypePath[IntAttrType]

  sealed trait AttrPath[T]:
    def links: Vector[Link]
    def dest: Attr[T]

  case class StrAttrPath(links: Vector[Link], dest: Attr[String]) extends AttrPath[String]
  case class IntAttrPath(links: Vector[Link], dest: Attr[Int])    extends AttrPath[Int]

  def apply[T](m: Model, p: StrAttrTypePath): Option[String] = 
    val sub = apply(m, p.links)
    apply(sub, p.dest)

  def apply(m: Model, sat: StrAttrType): Option[String]  = 
    m.elems.collectFirst{case sa: StrAttr if sa.at == sat => sa.value}

  def apply(m: Model, links: Seq[Link]): Model = links match
    case Seq() => Model()

    case Seq(link) => 
      val ms: Vector[Model] = link match 
        case e: Ent      => m.elems.collect{ case r: Rel if r.e == e && r.rt == Has => r.sub}
        case el: EntLink => m.elems.collect{ case r: Rel if r.e == el.e && r.rt == el.rt => r.sub}
      ms.foldLeft(Model())(_ ++ _)

    case Seq(link, rest*) => 
      val m2 = apply(m, Seq(link))
      apply(m2, rest.toVector)
  
