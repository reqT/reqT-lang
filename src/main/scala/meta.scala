package reqt

import scala.collection.immutable.ArraySeq

object meta:
  enum EntGroup:
    case ReqContext, GeneralReq, DataReq, FunctionalReq, QualityReq, DesignReq, VariabilityReq
  import EntGroup.* 

  /* since reqt 3.1.7:  
  -- Deleted entities: 
  Actor (use System or User), App (use Product or System), Class (use DataType), Data (use DataType), Domain (use Section), Epic (use Section), Item (use Text or Label), Member (use Field or Function), Meta (use Type), MockUp (use Prototype), Module (use Component), Scenario (use UseCase or UserStory), Service (use Function or Feature), Story (use UserStory), Term (use Text or Label), Test (use TestCase), Ticket (use Issue), WorkPackage (use Section or Issue)
    ++ Added entities:
  DataType, Field, Image, Prototype, TestCase, UserStory
    -- Deleted attributes: 
  Code, Constraints, FileName (use Location), Gist (use Spec or Text), Image (use entity Image and Location), Status (use Label)
    ++ Added attributes:
  Failure, Label, Location
    -- Deleted relations: 
  impacts (use relatesTo), interactsWith (use relatesTo or has), is (use supertypeOf in reverse order), superOf (use supertypeOf)
    ++ Added relations:
  supertypeOf
  */

  val entityConceptGroups: ArraySeq[((EntGroup,String),String)] = ArraySeq(
    ReqContext -> "Product" -> "An artifact offered to users or customers in an application domain, e.g. a software app or an embedded system.",
    ReqContext -> "Release" -> "A specific version of a product offered at a specific time to end users.",
    ReqContext -> "Resource" -> "A capability of, or support for product development, e.g. a development team or some testing equipment.",
    ReqContext -> "Risk" -> "Something negative that may happen.",
    ReqContext -> "Section" -> "A part of a requirements document or a subdomain.",
    ReqContext -> "Stakeholder" -> "A role, person or legal entity with a stake in the development or operation of a product.",
    ReqContext -> "System" -> "A set of software or hardware components interacting with users or systems.",
    ReqContext -> "User" -> "A human interacting with a system.",

    GeneralReq -> "Feature" -> "A releasable characteristic of a product. A (high-level, coherent) bundle of requirements.",
    GeneralReq -> "Goal" -> "An intention of a stakeholder or desired system property.",
    GeneralReq -> "Idea" -> "A concept or thought (potentially interesting).",
    GeneralReq -> "Image" -> "A visual representation, picture or diagram.",
    GeneralReq -> "Issue" -> "Something needed to be fixed or work to do.",
    GeneralReq -> "Req" -> "Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.",
    GeneralReq -> "TestCase" -> "A procedure to check if requirements are met.",

    DataReq -> "DataType" -> "An entity or a record stored in or processed by a system.",
    DataReq -> "Field" -> "An attribute that is part of a data type.",
    DataReq -> "Relationship" -> "A specific way that data types are connected.",

    DesignReq -> "Component" -> "A composable part of a system architecture. A reusable, interchangeable system unit or functionality.",
    DesignReq -> "Design" -> "A specific realization. A description of an implementation.",
    DesignReq -> "Prototype" -> "A system with limited functionality used to demonstrate a design idea.",
    DesignReq -> "Screen" -> "A design of (a part of) a user interface.",

    FunctionalReq -> "Event" -> "Something that can happen in the domain or in the system.",
    FunctionalReq -> "Function" -> "A description of how input is mapped to output. A capability of a system to do something specific.",
    FunctionalReq -> "Interface" -> "A defined way to interact with a system.",
    FunctionalReq -> "State" -> "A mode or condition of something in the domain or in the system. A configuration of data.",
    FunctionalReq -> "Task" -> "A piece of work by users, potentially supported by a system.",
    FunctionalReq -> "UseCase" -> "A list of steps defining interactions between actors and a system to achieve a goal.",
    FunctionalReq -> "UserStory" -> "A short description of what a user does or needs. Short for user story.",

    QualityReq -> "Barrier" -> "Something that makes it difficult to achieve a goal or a higher quality level.",
    QualityReq -> "Breakpoint" -> "A point of change, representing an important shift in the relation between quality and benefit.",
    QualityReq -> "Quality" -> "An aspect of system quality, distinguishing characteristic or degree of goodness.",
    QualityReq -> "Target" -> "A desired quality level or quality goal.",

    VariabilityReq -> "Configuration" -> "A specific combination of variants.",
    VariabilityReq -> "Variant" -> "An object or system property that can be chosen from a set of options.",
    VariabilityReq -> "VariationPoint" -> "An opportunity of choice among variants.",
  )

  val entityConcepts: ArraySeq[(String,String)] = entityConceptGroups.map((gn, d) => gn._2 -> d).sorted

  val strAttrConcepts = ArraySeq[(String,String)](
    "Comment" -> "A note with a remark or a discussion on an entity.",
    "Deprecated" -> "A description of why an entity should be avoided, often because it is superseded by another entity, as indicated by a 'deprecates' relation.",
    "Example" -> "A description that illustrates some entity by a typical instance.",
    "Expectation" -> "A required output of a test in order to be counted as passed.",
    "Failure" -> "A description of an error that prevents the normal execution of a system.",
    "Gist" -> "A short and simple description. A summary capturing the essence of an entity.",
    "Input" -> "Data consumed by an entity, ",
    "Label" -> "A descriptive tag used to classify something.",
    "Location" -> "A location of a resource such as a web address or a path to a file of persistent data.",
    "Output" -> "Data produced by an entity, e.g. a function or a test.",
    "Spec" -> "A definition of an entity. Short for specification",
    "Text" -> "An paragraph or general description.", 
    "Title" -> "A short descriptive heading.", 
    "Why" -> "A description of intention or rationale of an entity.",
  ).sorted

  val intAttrConcepts = ArraySeq[(String,String)](
    "Benefit" -> "A characterization of a good or helpful result or effect (e.g. of a feature).",
    "Capacity" -> "The largest amount that can be held or contained (e.g. by a resource).",
    "Cost" -> "The expenditure of something, such as time or effort, necessary for the implementation of an entity.",
    "Damage" -> "A characterization of the negative consequences if some entity (e.g. a risk) occurs.",
    "Frequency" -> "The rate of occurrence of some entity. ",
    "Max" -> "The maximum estimated or assigned (relative) value.",
    "Min" -> "The minimum estimated or assigned (relative) value.",
    "Order" -> "The ordinal number of an entity (1st, 2nd, ...).",
    "Prio" -> "The level of importance of an entity. Short for priority.",
    "Probability" -> "The likelihood that something (e.g. a risk) occurs.",
    "Profit" -> "The gain or return of some entity, e.g. in monetary terms.",
    "Value" -> "Some general integer value.",
  ).sorted

  val relationConcepts = ArraySeq[(String,String)](
    "binds" -> "Ties a value to an option. A configuration binds a variation point.",
    "deprecates" -> "Makes outdated. An entity deprecates (supersedes) another entity.",
    "excludes" -> "Prevents an entity combination. One feature excludes another in a release.",
    "has" -> "Expresses containment, substructure, composition or aggregation. An entity contains another entity.",
    "helps" -> "Positive influence. A goal supports the fulfillment of another goal.",
    "hurts" -> "Negative influence. A goal hinders another goal.",
    "implements" -> "Realisation of an entity, e.g. a module implements a feature.",
    "precedes" -> "Temporal ordering. A feature precedes (is implemented before) another feature.",
    "relatesTo" -> "Some general relation to another entity.",
    "requires" -> "A requested combination. One function requires that a another function is also implemented.",
    "supertypeOf" -> "Super-typing, generalization, includes another more specific entity. One data entity is a supertype of another.",
    "verifies" -> "Gives evidence of correctness. A test verifies the implementation of a feature.",
  ).sorted

  case class Concept(name: String, description: String, abstractType: String)

  val concepts: ArraySeq[Concept] = 
    (entityConcepts.map((n, d) => Concept(n, d, "EntityType")) ++
    strAttrConcepts.map((n, d) => Concept(n,  d, "StrAttrType")) ++
    intAttrConcepts.map((n, d) => Concept(n,  d, "IntAttrType")) ++
    relationConcepts.map((n, d) => Concept(n,  d, "RelationType"))).sortBy(_.name)

  val conceptMap: Map[String, Concept] = concepts.map(c => (c.name, c)).toMap

  def describe(name: String): String = conceptMap.get(name) match
    case Some(Concept(name, descr, abstractType)) => s"$descr [$abstractType]"
    case None => s"Unknown concept: $name"
  
  extension (concept: Any) def help: String = describe(concept.toString)

  extension (concepts: ArraySeq[(String, String)]) def names: ArraySeq[String] = concepts.map(_._1)

  val entityNames: ArraySeq[String]   = entityConcepts.names
  val strAttrNames: ArraySeq[String]  = strAttrConcepts.names
  val intAttrNames: ArraySeq[String]  = intAttrConcepts.names
  val relationNames: ArraySeq[String] = relationConcepts.names
  val conceptNames: ArraySeq[String]  = concepts.map(_.name)

  val isConceptName: Set[String] = conceptNames.toSet

  val entTypes: Map[String, EntType] = EntType.values.map(e => e.toString -> e).toMap
  val strAttrTypes: Map[String, StrAttrType] = StrAttrType.values.map(e => e.toString -> e).toMap
  val intAttrTypes: Map[String, IntAttrType] = IntAttrType.values.map(e => e.toString -> e).toMap
  val relTypes: Map[String, RelType] = RelType.values.map(e => e.toString.deCapitalize -> e).toMap
  val nodeTypes: Map[String, NodeType] = entTypes ++ strAttrTypes ++ intAttrTypes
  
  extension (s: String)
    inline def isEntType: Boolean     = meta.entTypes.isDefinedAt(s)
    inline def isStrAttrType: Boolean = meta.strAttrTypes.isDefinedAt(s)
    inline def isIntAttrType: Boolean = meta.intAttrTypes.isDefinedAt(s)
    inline def isNodeType: Boolean    = meta.nodeTypes.isDefinedAt(s)
    inline def isRelType: Boolean     = meta.relTypes.isDefinedAt(s)
    def deCapitalize: String = s.take(1).toLowerCase ++ s.drop(1)


  def generate: String = 
    def method(n: String): String = 
      s"|    def $n(sub: Elem*): Rel = Rel(e, ${n.capitalize}, Model(sub*))\n" +
      s"|    def $n: EntLink = EntLink(e, ${n.capitalize})\n"

    s"""|//--- THIS IS A GENERATED FILE! DO NOT EDIT `lang-GENERATED.scala` 
        |//--- EDIT the code below by changing `def generate` in file meta.scala
        |//--- GENERATE this file in sbt> `Test / runMain generateMeta`
        |//--- or by `println(reqt.meta.generate)` and copy-paste
        |
        |package reqt
        |
        |// the exports and extensions below defines the surface api
        |export lang.*
        |export Show.show
        |export selection.*
        |export Path.*
        |export parser.{m, toModel, p}
        |
        |extension (elems: Vector[Elem]) 
        |  def toModel = Model(elems)
        |  def m       = Model(elems)
        |
        |object lang:
        |  sealed trait Elem
        |  sealed trait Node extends Elem
        |
        |  trait ElemType
        |  trait NodeType extends ElemType
        |  trait AttrType[T] extends NodeType
        |
        |  sealed trait Link
        |  final case class EntLink(e: Ent, rt: RelType) extends Link
        |  final case class Ent private (et: EntType, id: String) extends Node, Link
        |  object Ent:
        |    val emptyId = "???"
        |    def apply(et: EntType, id: String): Ent = 
        |      new Ent(et, if id.isEmpty then emptyId else id)
        |
        |  final case class LinkType(et: EntType, rt: RelType) 
        |
        |  sealed trait Attr[T] extends Node:
        |    def at: AttrType[T]
        |    def value: T
        |
        |  final case class StrAttr(at: StrAttrType, value: String) extends Attr[String]
        |  final case class IntAttr(at: IntAttrType, value: Int) extends Attr[Int]
        |  final case class Undefined[T](at: AttrType[T]) extends Attr[T]:
        |    def value: T = throw new java.util.NoSuchElementException
        |
        |  final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
        |    def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
        |    def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
        |    def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }
        |
        |  final case class Model(elems: Vector[Elem]) extends ModelOps
        |  object Model extends ModelCompanionOps
        |
        |  enum EntType extends NodeType:
        |    case ${entityNames.mkString(",")}
        |  
        |  enum StrAttrType extends AttrType[String]:
        |    case ${strAttrNames.mkString(",")}
        |  
        |  enum IntAttrType extends AttrType[Int]:
        |    case ${intAttrNames.mkString(",")}
        |  
        |  enum RelType extends ElemType:
        |    case ${relationNames.map(_.capitalize).mkString(",")}
        |  
        |  export EntType.*
        |  export StrAttrType.*
        |  export IntAttrType.*
        |  export RelType.*
        |  
        |  extension (et: EntType)      def apply(id: String): Ent = Ent(et, id)
        |  extension (sat: StrAttrType) def apply(value: String): StrAttr = StrAttr(sat, value)
        |  extension (sat: IntAttrType) def apply(value: Int):    IntAttr = IntAttr(sat, value)
        |  extension (e: Ent)
        |""".stripMargin ++ relationNames.map(method).mkString("  ","\n  ", "").stripMargin