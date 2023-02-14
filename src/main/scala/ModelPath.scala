package reqt

object ModelPath:

  // TODO is EntLink really needed? Can we use a Rel with empty sub

  case class EntLink(e: Ent, rt: RelType):
    override def toString = s"$e & $rt"

  case class EntTypeLink(et: EntType, rt: RelType):
    override def toString = s"$et & $rt"
  
  type Link = Ent | EntType | EntLink | EntTypeLink

  extension (l1: Link) 
    def /(l2: Link): Vector[Link] = Vector(l1, l2)
    def /(a: Attr[String]): StrAttrPath = StrAttrPath(Vector(l1), a)
    def /(a: Attr[Int]):    IntAttrPath = IntAttrPath(Vector(l1), a)

  extension (ls: Vector[Link]) 
    def /(l: Link): Vector[Link] = ls :+ l
    def /(a: Attr[String]): StrAttrPath = StrAttrPath(ls, a)
    def /(a: Attr[Int]):    IntAttrPath = IntAttrPath(ls, a)


  case class StrAttrTypePath(links: Vector[Link], attr: StrAttrType)
  case class IntAttrTypePath(links: Vector[Link], attr: IntAttrType)

  case class StrAttrPath(links: Vector[Link], attr: Attr[String])
  case class IntAttrPath(links: Vector[Link], attr: Attr[Int])


  