package reqt

object path:
  type Link = Ent | EntLink 

  extension (l1: Link) 
    def /(l2: Link): Vector[Link] = Vector(l1, l2)
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](Vector(l1), a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](Vector(l1), a)

  extension (ls: Vector[Link]) 
    def /(l: Link): Vector[Link] = ls :+ l
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](ls, a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](ls, a)

  sealed trait Path[T]:
    def links: Vector[Link]
    def dest: AttrType[T] | Attr[T]

  final case class AttrTypePath[T](links: Vector[Link], dest: AttrType[T]) extends Path[T]
  final case class AttrPath[T](links: Vector[Link], dest: Attr[T]) extends Path[T]
