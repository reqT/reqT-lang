package reqt

import scala.collection.immutable.ArraySeq

object meta:
  enum EntGroup:
    case ReqContext, GeneralReq, DataReq, FunctionalReq, QualityReq, DesignReq, VariabilityReq
  import EntGroup.* 

  val entityConceptGroups: ArraySeq[((EntGroup,String),String)] = ArraySeq(
    ReqContext -> "Domain" -> "The application area of a product with its surrounding entities, e.g. users or other systems.",
    ReqContext -> "Product" -> "An artifact offered to users or customers, e.g. an app, service or  embedded system.",
    ReqContext -> "Release" -> "A specific version of a product offered to end users at a specific time.",
    ReqContext -> "Resource" -> "A capability of, or support for product development, e.g. a development team or some testing equipment.",
    ReqContext -> "Risk" -> "Something negative that may happen.",
    ReqContext -> "Section" -> "A part of a requirements document.",
    ReqContext -> "Stakeholder" -> "A role, person or legal entity with a stake in the development or operation of a product.",
    ReqContext -> "System" -> "A set of software or hardware components interacting with users or systems.",
    ReqContext -> "Term" -> "A word or group of words having a particular meaning in a particular domain.",
    ReqContext -> "User" -> "A human interacting with a system.",

    GeneralReq -> "Epic" -> "A coherent collection of features, stories, use cases or issues. A large part of a release.",
    // https://www.atlassian.com/agile/project-management/epics-stories-themes
    GeneralReq -> "Feature" -> "A releasable characteristic of a product. A (high-level, coherent) bundle of requirements.",
    GeneralReq -> "Goal" -> "An intention of a stakeholder or desired system property.",
    GeneralReq -> "Idea" -> "A concept or thought (potentially interesting).",
    GeneralReq -> "Image" -> "A visual representation, picture or diagram.",
    GeneralReq -> "Interface" -> "A defined way to interact with a system.",
    GeneralReq -> "Issue" -> "Something needed to be fixed or work to do.",
    GeneralReq -> "Label" -> "A descriptive tag used to classify something.",
    GeneralReq -> "Req" -> "Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.",
    GeneralReq -> "Test" -> "A procedure to check if requirements are met.",  // Or TestCase ?


    DataReq -> "Data" -> "A data entity, type, class, or record stored or processed by a system.", // or Data or DataEntity or DataType?
    DataReq -> "Class" -> "An extensible template for creating objects. A set of objects with certain attributes in common. A category.",  // somewhat redundant with Data but the latter is more general
    DataReq -> "Member" -> "A data entity that is part of another entity, eg. a field or method in a class",  // or DataField or DataAttribute or DataProperty
    DataReq -> "Relationship" -> "A specific way that data types are connected.", // or Association or Relation or DataRelation

    DesignReq -> "Component" -> "A composable part of a system architecture. A reusable, interchangeable system unit or functionality.",
    // https://softwareengineering.stackexchange.com/questions/178927/is-there-a-difference-between-a-component-and-a-module
    DesignReq -> "Design" -> "A specific realization. A description of an implementation.",
    DesignReq -> "Module" -> "A collection of coherent functions and interfaces.",
    DesignReq -> "Prototype" -> "A system with limited functionality used to demonstrate a design idea.",
    DesignReq -> "Screen" -> "A design of (a part of) a user interface.",

    FunctionalReq -> "Event" -> "Something that can happen in the domain or in the system.",
    FunctionalReq -> "Function" -> "A description of how input is mapped to output. A capability of a system to do something specific.",
    FunctionalReq -> "State" -> "A mode or condition of something in the domain or in the system. A configuration of data.",
    FunctionalReq -> "Task" -> "A piece of work by users, potentially supported by a system.",
    FunctionalReq -> "UseCase" -> "A goal-fulfilling interaction between users and a product in a specific usage context.",
    FunctionalReq -> "Story" -> "A description of what a user persona wants in order to achieve a goal. Short for user story.",

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
    "Constraints" -> "A collection of propositions that constrain a solution space or restrict possible attribute values.",
    "Deprecated" -> "A description of why an entity should be avoided, often because it is superseded by another entity, as indicated by a 'deprecates' relation.",
    "Example" -> "A description that illustrates some entity by a typical instance.",
    "Expectation" -> "A required output of a test in order to be counted as passed.",
    "Failure" -> "An error that prevents the normal execution of a system.",
    "Gist" -> "A short and simple description. A summary capturing the essence of an entity.",
    "Title" -> "A general or descriptive heading. One or more leading # indicate heading level.",
    "Input" -> "Data consumed by an entity, ",
    "Location" -> "A location of a resource such as a web address or a path to a file of persistent data.",
    "Output" -> "Data produced by an entity, e.g. a function or a test.",
    "Spec" -> "A definition of an entity. Short for specification",
    "Text" -> "An paragraph or general description.", 
    "Why" -> "A description of intention or rationale of an entity.",
  ).sorted

  val intAttrConcepts = ArraySeq[(String,String)](
    "Benefit" -> "A characterization of a good or helpful result or effect (e.g. of a feature).",
    "Capacity" -> "An amount that can be held or contained (e.g. by a resource).",
    "Cost" -> "An expenditure of something, such as time or effort, necessary for the implementation of an entity.",
    "Damage" -> "A characterization of the negative consequences if some entity (e.g. a risk) occurs.",
    "Frequency" -> "A number of occurrences per time unit. ",
    "Max" -> "A maximum estimated or assigned value.",
    "Min" -> "A minimum estimated or assigned value.",
    "Order" -> "An ordinal number of an entity (1st, 2nd, ...).",
    "Prio" -> "A level of importance of an entity. Short for priority.",
    "Probability" -> "A likelihood expressed as whole percentages that something (e.g. a risk) occurs.",
    "Profit" -> "A gain or return of some entity, e.g. in monetary terms.",
    "Value" -> "Some general integer value.",
  ).sorted

  val relationConcepts = ArraySeq[(String,String)](
    "binds" -> "Ties a value to an option. A configuration binds a variation point.",
    "deprecates" -> "Makes outdated. An entity deprecates (supersedes) another entity.",
    "excludes" -> "Prevents an entity combination. One feature excludes another in a release.",
    "has" -> "Expresses containment, substructure, composition or aggregation. An entity contains another entity.",
    "helps" -> "Positive influence. A goal supports the fulfillment of another goal.",
    "hurts" -> "Negative influence. A goal hinders another goal.",
    "impacts" -> "Some unspecific influence. A new feature impacts an existing component.",
    "implements" -> "Realisation of an entity, e.g. a component implements a feature.",
    "interacts" -> "A communication relation. A user interacts with an interface.",
    "precedes" -> "Temporal ordering. A feature precedes (should be implemented before) another feature.",
    "relates" -> "Some general, unspecific relation to another entity.",
    "requires" -> "A requested combination. One function requires that a another function is also implemented.",
    "inherits" -> "One entity inherits properties of another entity. A specialization, extension or subtype relation. ",
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
    inline def isEntType: Boolean     = entTypes.isDefinedAt(s)
    inline def isStrAttrType: Boolean = strAttrTypes.isDefinedAt(s)
    inline def isIntAttrType: Boolean = intAttrTypes.isDefinedAt(s)
    inline def isNodeType: Boolean    = nodeTypes.isDefinedAt(s)
    inline def isRelType: Boolean     = relTypes.isDefinedAt(s)
    inline def isElemStart: Boolean   = isConceptName(s.skipIndent.takeWhile(ch => !(ch.isSpaceChar || ch == '\t')))


  def generate: String = 
    def entExtensions(name: String): String = 
      s"|  def $name(sub: Elem*): Rel = Rel(e, ${name.capitalize}, Model(sub*))\n" +
      s"|  def $name: EntLink = EntLink(e, ${name.capitalize})\n"

    s"""|//--- THIS IS A GENERATED FILE! DO NOT EDIT AS CHANGES ARE LOST ON RE-GENERATION 
        |//--- Edit the code below by changing `def generate` in reqt.meta
        |//--- Generate this file in sbt> `meta`
        |//--- or by `println(reqt.meta.generate)` in sbt console and copy-paste
        |
        |package reqt
        |
        |final case class Model(elems: Vector[Elem]) extends ModelMembers
        |object Model extends ModelCompanion
        |
        |sealed trait Elem
        |sealed trait Node extends Elem
        |
        |sealed trait ElemType
        |sealed trait NodeType extends ElemType
        |sealed trait AttrType[T] extends NodeType
        |
        |sealed trait Link
        |final case class EntLink(e: Ent, rt: RelType) extends Link
        |
        |final case class Ent private (et: EntType, id: String) extends Node, Link
        |object Ent:
        |  val emptyId = "???"
        |  def apply(et: EntType, id: String): Ent = 
        |    new Ent(et, if id.isEmpty then emptyId else id)
        |
        |final case class LinkType(et: EntType, rt: RelType) 
        |
        |sealed trait Attr[T] extends Node:
        |  def at: AttrType[T]
        |  def value: T
        |
        |final case class StrAttr(at: StrAttrType, value: String) extends Attr[String]
        |final case class IntAttr(at: IntAttrType, value: Int) extends Attr[Int]
        |final case class Undefined[T](at: AttrType[T]) extends Attr[T]:
        |  def value: T = throw new java.util.NoSuchElementException
        |
        |final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
        |  def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
        |  def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
        |  def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }
        |
        |enum EntType extends NodeType:
        |  case ${entityNames.mkString(", ")}
        |
        |enum StrAttrType extends AttrType[String]:
        |  case ${strAttrNames.mkString(", ")}
        |
        |enum IntAttrType extends AttrType[Int]:
        |  case ${intAttrNames.mkString(", ")}
        |
        |enum RelType extends ElemType:
        |  case ${relationNames.map(_.capitalize).mkString(", ")}
        |
        |export EntType.*
        |export StrAttrType.*
        |export IntAttrType.*
        |export RelType.*
        |
        |extension (et: EntType)      def apply(id: String): Ent = Ent(et, id)
        |extension (sat: StrAttrType) def apply(value: String): StrAttr = StrAttr(sat, value)
        |extension (sat: IntAttrType) def apply(value: Int):    IntAttr = IntAttr(sat, value)
        |extension (e: Ent)
        |""".stripMargin ++ relationNames.map(entExtensions).mkString("","\n", "").stripMargin