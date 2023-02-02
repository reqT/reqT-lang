package reqt

object lang extends GENERATED:
  export EntType.*
  export StrAttrType.*
  export IntAttrType.*
  export RelType.*

  trait ElemType
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

  extension (sc: StringContext)
    def m(args: Any*): Model = Model(Feature("interpolator TODO"))