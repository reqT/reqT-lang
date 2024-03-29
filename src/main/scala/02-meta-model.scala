package reqt

import scala.collection.immutable.ArraySeq

object meta:
  trait ConceptGroup

  enum EntGroup extends ConceptGroup:
    case ContextEnt, GeneralEnt, DataEnt, FunctionalEnt, QualityEnt, DesignEnt, VariabilityEnt
  import EntGroup.* 

  enum RelGroup extends ConceptGroup:
    case GeneralRel, GoalRel, VariabilityRel, ClassRel, DependencyRel, ContextRel 
  import RelGroup.* 

  val entityConceptGroups: ArraySeq[((EntGroup,String),String)] = ArraySeq(
    ContextEnt -> "Domain" -> "The application area of a product with its surrounding entities, e.g. users or other systems.",
    ContextEnt -> "Product" -> "An artifact offered to users or customers, e.g. an app, service or  embedded system.",
    ContextEnt -> "Release" -> "A specific version of a product offered to end users at a specific time.",
    ContextEnt -> "Resource" -> "A capability of, or support for product development, e.g. a development team or some testing equipment.",
    ContextEnt -> "Risk" -> "Something negative that may happen.",
    ContextEnt -> "Section" -> "A part of a requirements document.",
    ContextEnt -> "Stakeholder" -> "A role, person or legal entity with a stake in the development or operation of a product.",
    ContextEnt -> "System" -> "A set of software or hardware components interacting with users or systems.",
    ContextEnt -> "Term" -> "A word or group of words having a particular meaning in a particular domain.",
    ContextEnt -> "User" -> "A human interacting with a system.",

    GeneralEnt -> "Epic" -> "A coherent collection of features, stories, use cases or issues. A large part of a release.",
    // https://www.atlassian.com/agile/project-management/epics-stories-themes
    GeneralEnt -> "Feature" -> "A releasable characteristic of a product. A high-level, coherent bundle of requirements.",
    GeneralEnt -> "Goal" -> "An intention of a stakeholder or desired system property.",
    GeneralEnt -> "Idea" -> "A concept or thought (potentially interesting).",
    GeneralEnt -> "Image" -> "A visual representation, picture or diagram.",
    GeneralEnt -> "Interface" -> "A defined way to interact with a system.",
    GeneralEnt -> "Issue" -> "Something needed to be fixed or work to do.",
    GeneralEnt -> "Label" -> "A descriptive tag used to classify something.",
    GeneralEnt -> "Req" -> "Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.",
    GeneralEnt -> "Test" -> "A procedure to check if requirements are met.",  // Or TestCase ?


    DataEnt -> "Data" -> "A data entity, type, class, or record stored or processed by a system.", // or Data or DataEntity or DataType?
    DataEnt -> "Class" -> "An extensible template for creating objects. A set of objects with certain attributes in common. A category.",  // somewhat redundant with Data but the latter is more general
    DataEnt -> "Member" -> "A data entity that is part of another entity, eg. a field or method in a class",  // or DataField or DataAttribute or DataProperty
    DataEnt -> "Relationship" -> "A specific way that data types are connected.", // or Association or Relation or DataRelation

    DesignEnt -> "Component" -> "A composable part of a system architecture. A reusable, interchangeable system unit or functionality.",
    // https://softwareengineering.stackexchange.com/questions/178927/is-there-a-difference-between-a-component-and-a-module
    DesignEnt -> "Design" -> "A specific realization. A description of an implementation.",
    DesignEnt -> "Module" -> "A collection of coherent functions and interfaces.",
    DesignEnt -> "Prototype" -> "A system with limited functionality used to demonstrate a design idea.",
    DesignEnt -> "Screen" -> "A design of (a part of) a user interface.",

    FunctionalEnt -> "Event" -> "Something that can happen in the domain or in the system.",
    FunctionalEnt -> "Function" -> "A description of how input is mapped to output. A capability of a system to do something specific.",
    FunctionalEnt -> "State" -> "A mode or condition of something in the domain or in the system. A configuration of data.",
    FunctionalEnt -> "Task" -> "A piece of work by users, potentially supported by a system. Short for user task",
    FunctionalEnt -> "UseCase" -> "A goal-fulfilling interaction between users and a product in a specific usage context.",
    FunctionalEnt -> "Story" -> "A description of what a user wants in order to achieve a goal. Short for user story.",

    QualityEnt -> "Barrier" -> "Something that makes it difficult to achieve a goal or a higher quality level.",
    QualityEnt -> "Breakpoint" -> "A point of change, representing an important shift in the relation between quality and benefit.",
    QualityEnt -> "Quality" -> "An aspect of system quality, distinguishing characteristic or degree of goodness.",
    QualityEnt -> "Target" -> "A desired quality level or quality goal.",

    VariabilityEnt -> "Configuration" -> "A specific combination of variants.",
    VariabilityEnt -> "Variant" -> "An object or system property that can be chosen from a set of options.",
    VariabilityEnt -> "VariationPoint" -> "An opportunity of choice among variants.",
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

  val relationConceptGroups = ArraySeq[((RelGroup,String),String)](
    ClassRel -> "inherits" -> "One entity inherits properties of another entity. A specialization, extension or subtype relation. ",
    ContextRel -> "interacts" -> "A communication relation. A user interacts with an interface.",
    DependencyRel -> "excludes" -> "Prevents an entity combination. One feature excludes another in a release.",
    DependencyRel -> "implements" -> "Realisation of an entity, e.g. a component implements a feature.",
    DependencyRel -> "precedes" -> "Temporal ordering. A feature precedes (should be implemented before) another feature.",
    DependencyRel -> "requires" -> "A requested combination. One function requires that a another function is also implemented.",
    DependencyRel -> "verifies" -> "Gives evidence of correctness. A test verifies the implementation of a feature.",
    GeneralRel -> "deprecates" -> "Makes outdated. An entity deprecates (supersedes) another entity.",
    GeneralRel -> "has" -> "Expresses containment, substructure, composition or aggregation. An entity contains another entity.",
    GeneralRel -> "impacts" -> "Some unspecific influence. A new feature impacts an existing component.",
    GeneralRel -> "relates" -> "Some general, unspecific relation to another entity.",
    GoalRel -> "helps" -> "Positive influence. A goal supports the fulfillment of another goal.",
    GoalRel -> "hurts" -> "Negative influence. A goal hinders another goal.",
    VariabilityRel -> "binds" -> "Ties a value to an option. A configuration binds a variation point.",
  )

  val relationConcepts: ArraySeq[(String,String)] = relationConceptGroups.map((gn, d) => gn._2 -> d).sorted

  case class Concept(name: String, descr: String, tpe: String, group: String)

  val concepts: ArraySeq[Concept] = (
    entityConceptGroups.map{case ((g,n), d) => Concept(n, d, "EntType", g.toString)} ++
    strAttrConcepts.map((n, d) => Concept(n,  d, "Attr","StrAttr")) ++
    intAttrConcepts.map((n, d) => Concept(n,  d, "Attr","IntAttr")) ++
    relationConceptGroups.map{case ((g,n), d) => Concept(n,  d, "RelType", g.toString)}
  ).sortBy(_.name)

  val conceptMap: Map[String, Concept] = concepts.map(c => (c.name, c)).toMap

  val groupMap: Map[String, ConceptGroup] = 
    (entityConceptGroups.map{case ((g,n),d) => (n, g)} 
      ++ relationConceptGroups.map{case ((g,n),d) => (n.capitalize, g)} 
      ++ strAttrConcepts.map{case (n, d) => (n,StrAttr) }
      ++ intAttrConcepts.map{case (n, d) => (n, IntAttr) }).toMap

  def describe(str: String): (String, Option[String]) = 
    val lower = str.toLowerCase
    val opt: Option[Concept] = conceptMap.get(lower)
    (if opt.isDefined then opt else conceptMap.get(lower.capitalize)) match
      case Some(Concept(name, descr, tpe, group)) => name -> Some(s"$descr [$tpe:$group]")
      case None => str -> None

  def similarConcepts(c: String, n: Int = 5): Seq[String] = conceptNames
      //.map(n => c.toLowerCase.sorted.diff(n.toLowerCase.sorted) -> n) //not as good as Levenshtein 
      .map(n => (if n.startsWith(c.toLowerCase.capitalize) then 1 else c.editDistanceTo(n)) -> n) 
      .sortBy(_._1)
      .take(n)
      .map(_._2)

  extension (concept: Any) 
    def help: String = 
      val (c, dOpt) = describe(concept.toString)
      dOpt.getOrElse(s"Unknown concept: $concept. Did you mean: ${similarConcepts(c).mkString(", ")}")
    
    def findConceptGroup: Option[ConceptGroup] = 
      val normalized = concept.toString.toLowerCase.capitalize
      groupMap.get(normalized)

  val entityNames: ArraySeq[String]   = entityConcepts.map(_._1)
  val strAttrNames: ArraySeq[String]  = strAttrConcepts.map(_._1)
  val intAttrNames: ArraySeq[String]  = intAttrConcepts.map(_._1)
  val relationNames: ArraySeq[String] = relationConcepts.map(_._1)
  val conceptNames: ArraySeq[String]  = concepts.map(_.name)

  val isConceptName: Set[String] = conceptNames.toSet

  val entTypes: Map[String, EntType] = EntType.values.map(e => e.toString -> e).toMap
  val strAttrTypes: Map[String, StrAttrType] = StrAttrType.values.map(e => e.toString -> e).toMap
  val intAttrTypes: Map[String, IntAttrType] = IntAttrType.values.map(e => e.toString -> e).toMap
  val relTypes: Map[String, RelType] = RelType.values.map(e => e.toString.deCapitalize -> e).toMap
  val nodeTypes: Map[String, NodeType] = entTypes ++ strAttrTypes ++ intAttrTypes
  
  extension (s: String)
    def isEntType: Boolean     = entTypes.isDefinedAt(s)
    def isStrAttrType: Boolean = strAttrTypes.isDefinedAt(s)
    def isIntAttrType: Boolean = intAttrTypes.isDefinedAt(s)
    def isAttrType: Boolean = isStrAttrType || isIntAttrType
    def isNodeType: Boolean    = nodeTypes.isDefinedAt(s)
    def isRelType: Boolean     = relTypes.isDefinedAt(s)
    def isElemStart: Boolean   = isConceptName(s.skipIndent.takeWhile(ch => !(ch.isSpaceChar || ch == '\t')))

  def matrix: Seq[Seq[String]] = for Concept(n, d, t, g) <- concepts yield Seq(n, t, g, d) 

  def csv(delim: String = ";"): String = 
    s"concept${delim}type${delim}group${delim}description\n" + matrix.map(_.mkString(delim)).mkString("\n")

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
        |case object StrAttr extends meta.ConceptGroup
        |
        |final case class IntAttr(at: IntAttrType, value: Int) extends Attr[Int], meta.ConceptGroup
        |case object IntAttr extends meta.ConceptGroup
        |
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