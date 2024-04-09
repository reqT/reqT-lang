package reqt

sealed trait Path:
  type Value 

  type Dest = AttrType[Value] | Attr[Value] | Ent | EntType

  def links: Vector[Link]

  def firstLinkDropped: Path

  def dest: Dest
  
  def hasDest: Boolean

  def valueOpt: Option[Value] = if !hasDest then None else dest match
    case u: Undefined[?] => None
    case a: Attr[Value]  => Some(a.value)
    case _ => None
  
  def toVector: Vector[Link | Dest] = if !hasDest then links else links :+ dest 

  def toModel: Model = 
    if links.isEmpty then
      if !hasDest then Model() else dest match
        case e: Ent => Model(e)
        case a: Attr[?]  => Model(a)
        case at: AttrType[?] => Model(Undefined(at))
        case et: EntType => Model(et.apply(""))
    else
      val h = links.head
      Model() + Rel(h.e, h.t, firstLinkDropped.toModel)

case object Path:
  def fromString(s: String): Path = ???  
  // TODO: parse path strings such as """Path/Feature("x")/Undefined(Prio)"""
    
  final case class AttrTypePath[T](links: Vector[Link], dest: AttrType[T]) extends Path:
    type Value = T
    def hasDest: Boolean = true
    def firstLinkDropped: AttrTypePath[T] = copy(links = links.tail)
  
  final case class AttrPath[T](links: Vector[Link], dest: Attr[T]) extends Path:
    type Value = T
    def hasDest: Boolean = true
    def firstLinkDropped: AttrPath[T] = copy(links = links.tail)

  final case class EntTypePath(links: Vector[Link], dest: EntType) extends Path:
    type Value = Nothing
    def hasDest: Boolean = true
    def firstLinkDropped: EntTypePath = copy(links = links.tail)

  final case class EntPath(links: Vector[Link], dest: Ent) extends Path:
    type Value = Nothing
    def hasDest: Boolean = true
    def firstLinkDropped: EntPath = copy(links = links.tail)

  final case class LinkPath(links: Vector[Link]) extends Path:
    type Value = Nothing
    def dest = throw java.util.NoSuchElementException()
    def hasDest: Boolean = false
    def firstLinkDropped: LinkPath = copy(links = links.tail)
  
  def /(link: Link): LinkPath = LinkPath(Vector(link))
  def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](Vector(), a)
  def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](Vector(), a)
  def /(e: Ent): EntPath = EntPath(Vector(), e)
  def /(e: EntType): EntTypePath = EntTypePath(Vector(), e)

  extension (l1: Link) 
    def /(l2: Link): LinkPath = LinkPath(Vector(l1, l2))
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](Vector(l1), a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](Vector(l1), a)
    def /(e: Ent): EntPath = EntPath(Vector(l1), e)
    def /(e: EntType): EntTypePath = EntTypePath(Vector(l1), e)

  extension (lp: LinkPath) 
    def /(l: Link): LinkPath = LinkPath(lp.links :+ l)
    def /[T](a: Attr[T]): AttrPath[T] = AttrPath[T](lp.links, a)
    def /[T](a: AttrType[T]): AttrTypePath[T] = AttrTypePath[T](lp.links, a)
    def /(e: Ent): EntPath = EntPath(lp.links, e)
    def /(e: EntType): EntTypePath = EntTypePath(lp.links, e)

  