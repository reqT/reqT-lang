package reqt

sealed trait Path[T]:
  def links: Vector[Link]
  def dest: AttrType[T] | Attr[T] | Nil.type
  def elems: Vector[Elem] = dest match
    case Nil => links
    case at: AttrType[?] => links :+ Undefined(at)
    case a: Attr[?] => links :+ a

object Path:
  final case class AttrTypePath[T](links: Vector[Link], dest: AttrType[T]) extends Path[T]
  final case class AttrPath[T](links: Vector[Link], dest: Attr[T]) extends Path[T]

  final case class LinkPath(links: Vector[Link]) extends Path[Nil.type]:
    def dest = Nil

  extension (l1: Link) 
    def /(l2: Link): LinkPath = LinkPath(Vector(l1, l2))
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](Vector(l1), a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](Vector(l1), a)

  extension (lp: LinkPath) 
    def /(l: Link): LinkPath = LinkPath(lp.links :+ l)
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](lp.links, a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](lp.links, a)

  object Root:
    def /(link: Link): LinkPath = LinkPath(Vector(link))
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](Vector(), a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](Vector(), a)
