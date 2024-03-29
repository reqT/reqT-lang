//--- THIS IS A GENERATED FILE! DO NOT EDIT AS CHANGES ARE LOST ON RE-GENERATION 
//--- Edit the code below by changing `def generate` in reqt.meta
//--- Generate this file in sbt> `meta`
//--- or by `println(reqt.meta.generate)` in sbt console and copy-paste

package reqt

final case class Model(elems: Vector[Elem]) extends ModelMembers
object Model extends ModelCompanion

sealed trait Elem
sealed trait Node extends Elem

sealed trait ElemType
sealed trait NodeType extends ElemType
sealed trait AttrType[T] extends NodeType

sealed trait Link
final case class EntLink(e: Ent, rt: RelType) extends Link

final case class Ent private (et: EntType, id: String) extends Node, Link
object Ent:
  val emptyId = "???"
  def apply(et: EntType, id: String): Ent = 
    new Ent(et, if id.isEmpty then emptyId else id)

final case class LinkType(et: EntType, rt: RelType) 

sealed trait Attr[T] extends Node:
  def at: AttrType[T]
  def value: T

final case class StrAttr(at: StrAttrType, value: String) extends Attr[String]
case object StrAttr extends meta.ConceptGroup

final case class IntAttr(at: IntAttrType, value: Int) extends Attr[Int], meta.ConceptGroup
case object IntAttr extends meta.ConceptGroup

final case class Undefined[T](at: AttrType[T]) extends Attr[T]:
  def value: T = throw new java.util.NoSuchElementException

final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
  def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
  def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
  def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }

enum EntType extends NodeType:
  case Barrier, Breakpoint, Class, Component, Configuration, Data, Design, Domain, Epic, Event, Feature, Function, Goal, Idea, Image, Interface, Issue, Label, Member, Module, Product, Prototype, Quality, Relationship, Release, Req, Resource, Risk, Screen, Section, Stakeholder, State, Story, System, Target, Task, Term, Test, UseCase, User, Variant, VariationPoint

enum StrAttrType extends AttrType[String]:
  case Comment, Constraints, Deprecated, Example, Expectation, Failure, Gist, Input, Location, Output, Spec, Text, Title, Why

enum IntAttrType extends AttrType[Int]:
  case Benefit, Capacity, Cost, Damage, Frequency, Max, Min, Order, Prio, Probability, Profit, Value

enum RelType extends ElemType:
  case Binds, Deprecates, Excludes, Has, Helps, Hurts, Impacts, Implements, Inherits, Interacts, Precedes, Relates, Requires, Verifies

export EntType.*
export StrAttrType.*
export IntAttrType.*
export RelType.*

extension (et: EntType)      def apply(id: String): Ent = Ent(et, id)
extension (sat: StrAttrType) def apply(value: String): StrAttr = StrAttr(sat, value)
extension (sat: IntAttrType) def apply(value: Int):    IntAttr = IntAttr(sat, value)
extension (e: Ent)
  def binds(sub: Elem*): Rel = Rel(e, Binds, Model(sub*))
  def binds: EntLink = EntLink(e, Binds)

  def deprecates(sub: Elem*): Rel = Rel(e, Deprecates, Model(sub*))
  def deprecates: EntLink = EntLink(e, Deprecates)

  def excludes(sub: Elem*): Rel = Rel(e, Excludes, Model(sub*))
  def excludes: EntLink = EntLink(e, Excludes)

  def has(sub: Elem*): Rel = Rel(e, Has, Model(sub*))
  def has: EntLink = EntLink(e, Has)

  def helps(sub: Elem*): Rel = Rel(e, Helps, Model(sub*))
  def helps: EntLink = EntLink(e, Helps)

  def hurts(sub: Elem*): Rel = Rel(e, Hurts, Model(sub*))
  def hurts: EntLink = EntLink(e, Hurts)

  def impacts(sub: Elem*): Rel = Rel(e, Impacts, Model(sub*))
  def impacts: EntLink = EntLink(e, Impacts)

  def implements(sub: Elem*): Rel = Rel(e, Implements, Model(sub*))
  def implements: EntLink = EntLink(e, Implements)

  def inherits(sub: Elem*): Rel = Rel(e, Inherits, Model(sub*))
  def inherits: EntLink = EntLink(e, Inherits)

  def interacts(sub: Elem*): Rel = Rel(e, Interacts, Model(sub*))
  def interacts: EntLink = EntLink(e, Interacts)

  def precedes(sub: Elem*): Rel = Rel(e, Precedes, Model(sub*))
  def precedes: EntLink = EntLink(e, Precedes)

  def relates(sub: Elem*): Rel = Rel(e, Relates, Model(sub*))
  def relates: EntLink = EntLink(e, Relates)

  def requires(sub: Elem*): Rel = Rel(e, Requires, Model(sub*))
  def requires: EntLink = EntLink(e, Requires)

  def verifies(sub: Elem*): Rel = Rel(e, Verifies, Model(sub*))
  def verifies: EntLink = EntLink(e, Verifies)
