package reqt

object lang:
  sealed trait ElemType
  trait AttributeType extends ElemType

  sealed trait Elem

  final case class Entity(et: EntityType, id: String) extends Elem:
    override def toString = s"""$et($id)"""

  final case class Attribute[T](at: AttributeType, value: T) extends Elem:
    override def toString = s"""$at($value)"""

  final case class Relation(e: Entity, rt: RelationType, sub: Model) extends Elem:
    override def toString = s"""$e.${rt.toString.toLowerCase}(${sub.toVector.mkString(",")})"""

  final case class Model(toVector: Vector[Elem]):
    def ++(other: Model): Model = Model(toVector ++ other.toVector)
    override def toString: String = toVector.mkString("Model(",",",")")

  object Model:
    def apply(elems: Elem*): Model = Model(elems.toVector)

  class ModelBuilder():
    private val buf = scala.collection.mutable.ListBuffer.empty[Elem]
    def +=(e: Elem): ModelBuilder = {buf += e; this}
    def toModel: Model = Model(buf.toVector)

  enum EntityType extends ElemType:
    case Feature, Req, Stakeholder
  export EntityType.*

  extension (et: EntityType) def apply(id: String): Entity = Entity(et, id)

  enum StringAttributeType extends AttributeType:
    case Spec, Gist
  export StringAttributeType.*

  extension (sat: StringAttributeType) def apply(value: String): Attribute[String] = Attribute(sat, value)

  enum IntAttributeType extends AttributeType:
    case Prio, Benefit
  export IntAttributeType.*

  extension (sat: IntAttributeType) def apply(value: Int): Attribute[Int] = Attribute(sat, value)

  enum RelationType extends ElemType:
    case Has, Requires
  export RelationType.*

  extension (e: Entity)
    def has(sub: Elem*): Relation = Relation(e, Has, Model(sub*))
    def requires(sub: Elem*): Relation = Relation(e, Requires, Model(sub*))