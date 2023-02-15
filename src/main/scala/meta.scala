package reqt

import scala.collection.immutable.ArraySeq

object meta:
  val entityConcepts = ArraySeq[(String,String)](
    "Actor" -> "A human or machine that communicates with a system.",
    "App" -> "A computer program, or group of programs designed for end users, normally with a graphical user interface. Short for application." ,
    "Barrier" -> "Something that makes it difficult to achieve a goal or a higher quality level." ,
    "Breakpoint" -> "A point of change. An important aspect of a (non-linear) relation between quality and benefit.",
    "Class" -> "An extensible template for creating objects. A set of objects with certain attributes in common. A category.",
    "Component" -> "A composable part of a system. A reusable, interchangeable system unit or functionality." ,
    "Configuration" -> "A specific combination of variants." ,
    "Data" -> "Information stored in a system." ,
    "Design" -> "A specific realization or high-level implementation description (of a system part).",
    "Domain" -> "The application area of a product with its surrounding entities.",
    "Epic" -> "A large user story or a collection of stories." ,
    "Event" -> "Something that can happen in the domain and/or in the system.",
    "Feature" -> "A releasable characteristic of a product. A (high-level, coherent) bundle of requirements.",
    "Function" -> "A description of how input data is mapped to output data. A capability of a system to do something specific." ,
    "Goal" -> "An intention of a stakeholder or desired system property." ,
    "Idea" -> "A concept or thought (potentially interesting)." ,
    "Interface" -> "A defined way to interact with a system." ,
    "Item" -> " An article in a collection, enumeration, or series.",
    "Issue" -> "Something needed to be fixed." ,
    "Label" -> "A descriptive name used to identify something.",
    "Meta" -> "A prefix used on a concept to mean beyond or about its own concept, e.g. metadata is data about data.",
    "Member" -> "An entity that is part of another entity, eg. a field in a in a class." ,
    "Module" -> "A collection of coherent functions and interfaces.",
    "MockUp" -> "A prototype with limited functionality used to demonstrate a design idea.",
    "Product" -> "Something offered to a market." ,
    "Quality" -> "A distinguishing characteristic or degree of goodness.",
    "Relationship" -> "A specific way that entities are connected." ,
    "Release" -> "A specific version of a system offered at a specific time to end users." ,
    "Req" -> "Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.",
    "Resource" -> "A capability of, or support for development." ,
    "Risk" -> "Something negative that may happen." ,
    "Scenario" -> "A (vivid) description of a (possible future) system usage." ,
    "Screen" -> "A design of (a part of) a user interface." ,
    "Section" -> "A part of a (requirements) document." ,
    "Service" -> "Actions performed by systems and/or humans to provide results to stakeholders." ,
    "Stakeholder" -> "Someone with a stake in the system development or usage." ,
    "State" -> "A mode or condition of something in the domain and/or in the system. A configuration of data.",
    "Story" -> "A short description of what a user does or needs. Short for user story." ,
    "System" -> "A set of interacting software and/or hardware components." ,
    "Target" -> "A desired quality level or goal ." ,
    "Task" -> "A piece of work (that users do, maybe supported by a system)." ,
    "Term" -> "A word or group of words having a particular meaning.",
    "Test" -> "A procedure to check if requirements are met." ,
    "Ticket" -> "(Development) work awaiting to be completed." ,
    "UseCase" -> "A list of steps defining interactions between actors and a system to achieve a goal." ,
    "User" -> "A human interacting with a system." ,
    "Variant" -> "An object or system property that can be chosen from a set of options." ,
    "VariationPoint" -> "An opportunity of choice among variants.",
    "WorkPackage" -> "A collection of (development) work tasks.",
  )

  val strAttrConcepts = ArraySeq[(String,String)](
    "Comment" -> "A note that explains or discusses some entity." ,
    "Deprecated" -> "A description of why an entity should be avoided, often because it is superseded by another entity, as indicated by a 'deprecates' relation." ,
    "Example" -> "A note that illustrates some entity by a  typical instance." ,
    "Expectation" -> "The required output of a test in order to be counted as passed." ,
    "Err" -> "An error message explaining a failure.",
    "FileName" -> "The name of a storage of serialized, persistent data." ,
    "Gist" -> "A short and simple description of an entity, e.g. a function or a test." ,
    "Image" -> "(The name of) a picture of an entity." ,
    "Input" -> "Data consumed by an entity, " ,
    "Output" -> "Data produced by an entity, e.g. a function or a test." ,
    "Spec" -> "A (detailed) definition of an entity. Short for specification" ,
    "Status" -> "A level of refinement of an entity (e.g. a feature) in the development process. ", 
    "Text" -> "A sequence of words (in natural language).", 
    "Title" -> "A general or descriptive heading.", 
    "Why" -> "A description of intention. Rationale.",
  )

  val intAttrConcepts = ArraySeq[(String,String)](
    "Benefit" -> "A characterization of a good or helpful result or effect (e.g. of a feature)." ,
    "Capacity" -> "The largest amount that can be held or contained (e.g. by a resource)." ,
    "Cost" -> "The expenditure of something, such as time or effort, necessary for the implementation of an entity." ,
    "Damage" -> "A characterization of the negative consequences if some entity (e.g. a risk) occurs." ,
    "Frequency" -> "The rate of occurrence of some entity. " ,
    "Max" -> "The maximum estimated or assigned (relative) value." ,
    "Min" -> "The minimum estimated or assigned (relative) value." ,
    "Order" -> "The ordinal number of an entity (1st, 2nd, ...)." ,
    "Prio" -> "The level of importance of an entity. Short for priority." ,
    "Probability" -> "The likelihood that something (e.g. a risk) occurs." ,
    "Profit" -> "The gain or return of some entity, e.g. in monetary terms." ,
    "Value" -> "An amount. An estimate of worth.",
  )

  val relationConcepts = ArraySeq[(String,String)](
    "binds" -> "Ties a value to an option. A configuration binds a variation point." ,
    "deprecates" -> "Makes outdated. An entity deprecates (supersedes) another entity." ,
    "excludes" -> "Prevents a combination. An entity excludes another entity." ,
    "has" -> "Expresses containment, substructure. An entity contains another entity." ,
    "helps" -> "Positive influence. A goal helps to fulfil another goal." ,
    "hurts" -> "Negative influence. A goal hinders another goal." ,
    "impacts" -> "Some influence. A new feature impacts an existing component." ,
    "implements" -> "Realisation of. A module implements a feature." ,
    "interactsWith" -> "Communication. A user interacts with an interface." ,
    "is" -> "Sub-typing, specialization, part of another, more general entity." ,
    "precedes" -> "Temporal ordering. A feature precedes (is implemented before) another feature." ,
    "relatesTo" -> "General relation. An entity is related to another entity." ,
    "requires" -> "Requested combination. An entity is required (or wished) by another entity." ,
    "superOf" -> "Super-typing, generalization, includes another, more specific entity." ,
    "verifies" -> "Gives evidence of correctness. A test verifies the implementation of a feature.",
  )

  case class Concept(name: String, description: String, abstractType: String)

  val concepts: ArraySeq[Concept] = 
    entityConcepts.map((n, d) => Concept(n, d, "EntityType")) ++
    strAttrConcepts.map((n, d) => Concept(n,  d, "StrAttrType")) ++
    intAttrConcepts.map((n, d) => Concept(n,  d, "IntAttrType")) ++
    relationConcepts.map((n, d) => Concept(n,  d, "RelationType"))

  val conceptMap: Map[String, Concept] = concepts.map(c => (c.name, c)).toMap

  def describe(name: String): String = conceptMap.get(name) match
    case Some(Concept(name, descr, abstractType)) => s"$descr [$abstractType]"
    case None => s"Unknown concept: $name"
  
  extension (concept: Any) def help: String = describe(concept.toString)

  extension (concepts: ArraySeq[(String, String)]) def names: ArraySeq[String] = concepts.map(_._1)

  val entityNames: ArraySeq[String] = entityConcepts.names
  val strAttrNames: ArraySeq[String] = strAttrConcepts.names
  val intAttrNames: ArraySeq[String] = intAttrConcepts.names
  val relationNames: ArraySeq[String] = relationConcepts.names
  val conceptNames: ArraySeq[String] = concepts.map(_.name)

  val isConceptName: Set[String] = conceptNames.toSet

  val entTypes: Map[String, EntType] = EntType.values.map(e => e.toString -> e).toMap
  val strAttrTypes: Map[String, StrAttrType] = StrAttrType.values.map(e => e.toString -> e).toMap
  val intAttrTypes: Map[String, IntAttrType] = IntAttrType.values.map(e => e.toString -> e).toMap
  val relTypes: Map[String, RelType] = RelType.values.map(e => e.toString.toLowerCase -> e).toMap
  val nodeTypes: Map[String, NodeType] = entTypes ++ strAttrTypes ++ intAttrTypes
  
  extension (s: String)
    inline def isEntType: Boolean     = meta.entTypes.isDefinedAt(s)
    inline def isStrAttrType: Boolean = meta.strAttrTypes.isDefinedAt(s)
    inline def isIntAttrType: Boolean = meta.intAttrTypes.isDefinedAt(s)
    inline def isNodeType: Boolean    = meta.nodeTypes.isDefinedAt(s)
    inline def isRelType: Boolean     = meta.relTypes.isDefinedAt(s)


  def generate: String = 
    def method(n: String): String = 
      s"|    def $n(sub: Elem*): Rel = Rel(e, ${n.capitalize}, Model(sub*))\n" +
      s"|    def $n: EntLink = EntLink(e, ${n.capitalize})\n"

    s"""|//!GENERATE this file in sbt> Test / runMain generateMeta
        |// or by `println(reqt.meta.generate)` in repl and copy-paste
        |package reqt
        |
        |object model:
        |  sealed trait Elem
        |  sealed trait Node extends Elem
        |
        |  trait ElemType
        |  trait NodeType extends ElemType
        |  trait AttrType[T] extends NodeType
        |
        |  final case class Ent private (et: EntType, id: String) extends Node
        |  object Ent:
        |    val emptyId = "???"
        |    def apply(et: EntType, id: String): Ent = 
        |      new Ent(et, if id.isEmpty then emptyId else id)
        |
        |  case class EntTypeLink(et: EntType, rt: RelType)
        |
        |  case class EntLink(e: Ent, rt: RelType)
        |
        |  sealed trait Attr[T] extends Node:
        |    def at: AttrType[T]
        |    def value: T
        |
        |  final case class StrAttr(at: StrAttrType, value: String) extends Attr[String]
        |  final case class IntAttr(at: IntAttrType, value: Int) extends Attr[Int]
        |
        |  final case class Rel(e: Ent, rt: RelType, sub: Model) extends Elem:
        |    def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
        |    def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
        |    def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, rt, Model(n)) }
        |
        |  final case class Model(elems: Vector[Elem]) extends ModelOps:
        |    override def toString: String = elems.mkString("Model(",",",")")
        |
        |  object Model:
        |    def apply(elems: Elem*): Model = Model(elems.toVector)
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