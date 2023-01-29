package reqt

object lang:
  sealed trait ElemType
  trait AttrType extends ElemType

  sealed trait Elem
  sealed trait Node extends Elem
  final case class Ent(et: EntType, id: String) extends Node:
    override def toString = s"""$et(${id.show})"""

  final case class Attr[T](at: AttrType, value: T) extends Node:
    override def toString = s"""$at(${value.show})"""

  final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
    override def toString = s"""$e.${rt.toString.toLowerCase}(${sub.toVector.mkString(",")})"""

  final case class Model(toVector: Vector[Elem]):
    def ++(other: Model): Model = Model(toVector ++ other.toVector)
    def +(e: Elem): Model = Model(toVector :+ e)
    override def toString: String = toVector.mkString("Model(",",",")")

  object Model:
    def apply(elems: Elem*): Model = Model(elems.toVector)

  class ModelBuilder():
    private val buf = scala.collection.mutable.ListBuffer.empty[Elem]
    def +=(e: Elem): ModelBuilder = {buf += e; this}
    def toModel: Model = Model(buf.toVector)

  enum EntType extends ElemType:
    case Feature, Req, Stakeholder
  export EntType.*

  extension (et: EntType) def apply(id: String): Ent = Ent(et, id)

  enum StrAttrType extends AttrType:
    case Spec, Gist
  export StrAttrType.*

  extension (sat: StrAttrType) def apply(value: String): Attr[String] = Attr(sat, value)

  enum IntAttrType extends AttrType:
    case Prio, Benefit
  export IntAttrType.*

  extension (sat: IntAttrType) def apply(value: Int): Attr[Int] = Attr(sat, value)

  enum RelType extends ElemType:
    case Has, Requires
  export RelType.*

  extension (e: Ent)
    def has(sub: Elem*): Rel = Rel(e, Has, Model(sub*))
    def requires(sub: Elem*): Rel = Rel(e, Requires, Model(sub*))