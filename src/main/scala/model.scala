package reqt

trait ElemType
trait AttrType[T] extends ElemType

sealed trait Elem

sealed trait Node extends Elem

final case class Ent(et: EntType, id: String) extends Node

final case class Attr[T <: Int | String](at: AttrType[T], value: T) extends Node

final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
  def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
  def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
  def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }

final case class Model(elems: Vector[Elem]):
  def ++(other: Model): Model = Model(elems ++ other.elems)
  def +(e: Elem): Model = Model(elems :+ e)
  def tip: Model = Model(elems.collect { case n: Node => n case Rel(e, _, _) => e }.distinct)
  def top: Model = Model(elems.collect { case n: Node => n case Rel(e, r, m) => Rel(e, r, m.tip) })
  override def toString: String = elems.mkString("Model(",",",")")

object Model:
  def apply(elems: Elem*): Model = Model(elems.toVector)

extension (sc: StringContext)
  def m(args: Any*): Model = 
    val strings: Iterator[String] = sc.parts.iterator
    val expressions: Iterator[Any] = args.iterator
    val sb = StringBuilder(strings.next)
    while strings.hasNext do
        sb.append(expressions.next.toString)
        sb.append(strings.next)
    sb.toString
    Model(Feature("interpolator TODO"))


