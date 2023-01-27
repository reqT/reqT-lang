package reqt


sealed trait ElemType
trait EntityType extends ElemType
trait AttributeType extends ElemType
trait StringAttributeType extends AttributeType
trait IntAttributeType extends AttributeType
trait RelationType extends ElemType

sealed trait Elem
final case class Entity(tpe: EntityType, id: String) extends Elem
final case class Attribute[T](tpe: AttributeType, value: T) extends Elem
final case class Relation(ent: Entity, tpe: RelationType, sub: Model) extends Elem

final case class Model(toVector: Vector[Elem]):
  def ++(other: Model): Model = Model(toVector ++ other.toVector)
  override def toString: String = toVector.mkString("Model(",",",")")

object Model:
  def apply(elems: Elem*): Model = Model(elems.toVector)

class ModelBuilder():
  private val buf = scala.collection.mutable.ListBuffer.empty[Elem]
  def +=(e: Elem): ModelBuilder = {buf += e; this}
  def toModel: Model = Model(buf.toVector)

enum BuiltinEntityTypes extends EntityType:
  case Feature, Req, Stakeholder

enum BuiltinStringAttributeTypes extends StringAttributeType:
  case Spec, Gist

enum BuiltinIntAttributeTypes extends IntAttributeType:
  case Prio, Benefit

enum BuiltinRelationTypes extends RelationType:
  case Has, Requires