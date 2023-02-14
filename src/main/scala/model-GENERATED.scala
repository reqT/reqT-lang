//!GENERATE this file in sbt> Test / runMain generateMeta
// or by `println(reqt.meta.generate)` in repl and copy-paste
package reqt

object model:
  sealed trait Elem
  sealed trait Node extends Elem

  trait ElemType
  trait NodeType extends ElemType
  trait AttrType[T] extends NodeType

  final case class Ent private (et: EntType, id: String) extends Node
  object Ent:
    val emptyId = "???"
    def apply(et: EntType, id: String): Ent = 
      new Ent(et, if id.isEmpty then emptyId else id)


  final case class Attr[T <: Int | String](at: AttrType[T], value: T) extends Node

  final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
    def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
    def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
    def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }

  final case class Model(elems: Vector[Elem]) extends ModelOps:
    override def toString: String = elems.mkString("Model(",",",")")

  object Model:
    def apply(elems: Elem*): Model = Model(elems.toVector)

  enum EntType extends NodeType:
    case Actor,App,Barrier,Breakpoint,Class,Component,Configuration,Data,Design,Domain,Epic,Event,Feature,Function,Goal,Idea,Interface,Item,Issue,Label,Meta,Member,Module,MockUp,Product,Quality,Relationship,Release,Req,Resource,Risk,Scenario,Screen,Section,Service,Stakeholder,State,Story,System,Target,Task,Term,Test,Ticket,UseCase,User,Variant,VariationPoint,WorkPackage
  
  enum StrAttrType extends AttrType[String]:
    case Comment,Deprecated,Example,Expectation,Err,FileName,Gist,Image,Input,Output,Spec,Status,Text,Title,Why
  
  enum IntAttrType extends AttrType[Int]:
    case Benefit,Capacity,Cost,Damage,Frequency,Max,Min,Order,Prio,Probability,Profit,Value
  
  enum RelType extends ElemType:
    case Binds,Deprecates,Excludes,Has,Helps,Hurts,Impacts,Implements,InteractsWith,Is,Precedes,RelatesTo,Requires,SuperOf,Verifies
  
  export EntType.*
  export StrAttrType.*
  export IntAttrType.*
  export RelType.*
  
  extension (et: EntType)      def apply(id: String): Ent = Ent(et, id)
  extension (sat: StrAttrType) def apply(value: String): Attr[String] = Attr(sat, value)
  extension (sat: IntAttrType) def apply(value: Int): Attr[Int] = Attr(sat, value)
  extension (e: Ent)
    def binds(sub: Elem*): Rel = Rel(e, Binds, Model(sub*))
    def deprecates(sub: Elem*): Rel = Rel(e, Deprecates, Model(sub*))
    def excludes(sub: Elem*): Rel = Rel(e, Excludes, Model(sub*))
    def has(sub: Elem*): Rel = Rel(e, Has, Model(sub*))
    def has: ModelPath.EntLink = ModelPath.EntLink(e, Has) //TODO
    def helps(sub: Elem*): Rel = Rel(e, Helps, Model(sub*))
    def hurts(sub: Elem*): Rel = Rel(e, Hurts, Model(sub*))
    def impacts(sub: Elem*): Rel = Rel(e, Impacts, Model(sub*))
    def implements(sub: Elem*): Rel = Rel(e, Implements, Model(sub*))
    def interactsWith(sub: Elem*): Rel = Rel(e, InteractsWith, Model(sub*))
    def is(sub: Elem*): Rel = Rel(e, Is, Model(sub*))
    def precedes(sub: Elem*): Rel = Rel(e, Precedes, Model(sub*))
    def relatesTo(sub: Elem*): Rel = Rel(e, RelatesTo, Model(sub*))
    def requires(sub: Elem*): Rel = Rel(e, Requires, Model(sub*))
    def superOf(sub: Elem*): Rel = Rel(e, SuperOf, Model(sub*))
    def verifies(sub: Elem*): Rel = Rel(e, Verifies, Model(sub*))