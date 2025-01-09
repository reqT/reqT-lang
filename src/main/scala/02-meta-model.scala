package reqt

import scala.collection.immutable.ArraySeq

object meta:
  trait ConceptGroup

  enum EntGroup extends ConceptGroup:
    case GeneralEnt, ContextEnt, DataEnt, FunctionalEnt, QualityEnt, DesignEnt, VariabilityEnt
  import EntGroup.* 

  enum RelGroup extends ConceptGroup:
    case GeneralRel, GoalRel, VariabilityRel, ClassRel, DependencyRel, ContextRel 
  import RelGroup.* 

  val entityConceptGroups: ArraySeq[((EntGroup,String),String)] = ArraySeq(
    ContextEnt -> "Actor" -> "A role played by a user or external system that interacts with the system (product, app, or service) under development.",
    ContextEnt -> "App" -> "A computer program, or group of programs designed for end users, normally with a graphical user interface. Short for application.",
    ContextEnt -> "Domain" -> "The application area of a product with its surrounding entities, e.g. users or other systems.",
    ContextEnt -> "Product" -> "An artifact offered to users or customers, e.g. an app, service or  embedded system.",
    ContextEnt -> "Release" -> "A specific version of a product offered to end users at a specific time.",
    ContextEnt -> "Resource" -> "A capability of, or support for product development, e.g. a development team or some testing equipment.",
    ContextEnt -> "Risk" -> "Something negative that may happen.",
    ContextEnt -> "Scenario" -> "A narrative of foreseeable interactions of user roles (actors) and the system (product, app, or service) under development..",
    ContextEnt -> "Service" -> "System use that provides value to stakeholders. System actions that stakeholders are willing to pay for.",
    ContextEnt -> "Stakeholder" -> "A role, person or legal entity with a stake in the development or operation of a product.",
    ContextEnt -> "System" -> "A set of software or hardware components interacting with users or systems.",
    ContextEnt -> "User" -> "A human interacting with a system.",

    GeneralEnt -> "Epic" -> "A coherent collection of features, stories, use cases or issues. A large part of a release.",
    // https://www.atlassian.com/agile/project-management/epics-stories-themes
    GeneralEnt -> "Feature" -> "A releasable characteristic of a product. A high-level, coherent bundle of requirements.",
    GeneralEnt -> "Goal" -> "An intention of a stakeholder or desired system property.",
    GeneralEnt -> "Idea" -> "A concept or thought, potentially interesting.",
    GeneralEnt -> "Image" -> "A visual representation, picture or diagram.",
    GeneralEnt -> "Interface" -> "A way to interact with a system.",
    GeneralEnt -> "Issue" -> "Something to be fixed or work to do.",
    GeneralEnt -> "Item" -> "An article in a collection, enumeration, or series.",
    GeneralEnt -> "Label" -> "A descriptive tag used to classify something.",
    GeneralEnt -> "Req" -> "Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.",
    GeneralEnt -> "Section" -> "A part of a requirements document.",
    GeneralEnt -> "Term" -> "A word or group of words having a particular meaning in a particular domain.",
    GeneralEnt -> "Test" -> "A procedure to check if requirements are met.", 
    GeneralEnt -> "WorkPackage" -> "A coherent collection of (development) activities.", 


    DataEnt -> "Data" -> "A data entity, type, class, or record stored or processed by a system.", // or Data or DataEntity or DataType?
    DataEnt -> "Class" -> "An extensible template for creating objects. A set of objects with certain attributes in common. A category.",  // somewhat redundant with Data but the latter is more general
    DataEnt -> "Field" -> "A data attribute that is part of another entity, such as a class.",  // or DataField or DataAttribute or DataProperty
    DataEnt -> "Member" -> "A data entity that is part of another entity. More specific alternatives: field, function.",  // or DataField or DataAttribute or DataProperty
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

  val entTypeGroupMap: Map[EntType, EntGroup] = entityConceptGroups.map((gn, d) => EntType.valueOf(gn._2) -> gn._1).toMap

  val entGroupTypesMap: Map[EntGroup, Set[EntType]] = entTypeGroupMap.toSeq.groupBy(_._2).map((x, xs) => x -> xs.map(_._1).toSet)

  val strAttrConcepts = ArraySeq[(String,String)](
    "Comment" -> "A note with a remark or a discussion on an entity.",
    "Constraints" -> "A collection of propositions that constrain a solution space or restrict possible attribute values.",
    "Deprecated" -> "A description of why an entity should be avoided, often because it is superseded by another entity, as indicated by a 'deprecates' relation.",
    "Example" -> "A description that illustrates some entity by a typical instance.",
    "Expectation" -> "A required output of a test in order to be counted as passed.",
    "Failure" -> "A description of a runtime error that prevents the normal execution of a system.",
    "Gist" -> "A short and simple description. A summary capturing the essence of an entity.",
    "Title" -> "A general or descriptive heading.",
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
    "Order" -> "An ordinal number (1st, 2nd, ...).",
    "Prio" -> "A level of importance of an entity. Short for priority.",
    "Probability" -> "A likelihood expressed as whole percentages that something (e.g. a risk) occurs.",
    "Profit" -> "A gain or return of some entity, e.g. in monetary terms.",
    "Value" -> "Some general integer value.",
  ).sorted

  val relationConceptGroups = ArraySeq[((RelGroup,String),String)](
    ClassRel -> "is" -> "One entity inherits properties of another entity. A specialization, extension or subtype relation. ",
    ContextRel -> "interactsWith" -> "A communication relation. A user interacts with an interface.",
    DependencyRel -> "excludes" -> "Prevents an entity combination. One feature excludes another in a release.",
    DependencyRel -> "implements" -> "Realisation of an entity, e.g. a component implements a feature.",
    DependencyRel -> "precedes" -> "Temporal ordering. A feature precedes (should be implemented before) another feature.",
    DependencyRel -> "requires" -> "A requested combination. One function requires that a another function is also implemented.",
    DependencyRel -> "verifies" -> "Gives evidence of correctness. A test verifies the implementation of a feature.",
    GeneralRel -> "deprecates" -> "Makes outdated. An entity deprecates (supersedes) another entity.",
    GeneralRel -> "has" -> "Expresses containment, substructure, composition or aggregation. An entity contains another entity.",
    GeneralRel -> "impacts" -> "Some unspecific influence. A new feature impacts an existing component.",
    GeneralRel -> "relatesTo" -> "Some general, unspecific relation to another entity.",
    GoalRel -> "helps" -> "Positive influence. A goal supports the fulfillment of another goal.",
    GoalRel -> "hurts" -> "Negative influence. A goal hinders another goal.",
    VariabilityRel -> "binds" -> "Ties a value to an option. A configuration binds a variation point.",
  )

  val relationConcepts: ArraySeq[(String,String)] = relationConceptGroups.map((gn, d) => gn._2 -> d).sorted

  case class Concept(name: String, descr: String, tpe: String, group: String)

  val generalConcepts: Seq[Concept] = Seq(
    Concept("Model", "A collection of model elements, which can be entities, attributes or relations.", "Model", "General")
  )

  val concepts: ArraySeq[Concept] = (
    entityConceptGroups.map{case ((g,n), d) => Concept(n, d, "EntType", g.toString)} ++
    strAttrConcepts.map((n, d) => Concept(n,  d, "Attr","StrAttr")) ++
    intAttrConcepts.map((n, d) => Concept(n,  d, "Attr","IntAttr")) ++
    relationConceptGroups.map{case ((g,n), d) => Concept(n,  d, "RelType", g.toString)} ++
    generalConcepts
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
      val fw = concept.toString.firstWord
      val (c, dOpt) = describe(fw)
      dOpt.getOrElse(s"Unknown concept: $fw. Did you mean: ${similarConcepts(c).mkString(", ")}")
    
    def findConceptGroup: Option[ConceptGroup] = 
      val fw = concept.toString.firstWord
      val normalized = fw.toLowerCase.capitalize
      groupMap.get(normalized)

  val entityNames: ArraySeq[String]   = entityConcepts.map(_._1)
  val strAttrNames: ArraySeq[String]  = strAttrConcepts.map(_._1)
  val intAttrNames: ArraySeq[String]  = intAttrConcepts.map(_._1)
  val attributeNames: ArraySeq[String]     = (strAttrNames ++ intAttrNames).sorted
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
    def isElemStart: Boolean   = isConceptName(s.skipIndent.takeWhile(ch => ch.isLetter)) //!(ch.isSpaceChar || ch == '\t')))

  def parseConcept(s: String): (Option[Elem | ElemType | Link], String) =
    val trimmed = s.trim
    val fw = trimmed.firstWord
    val rest1 = trimmed.drop(fw.length).trim
    if rest1.isEmpty then 
      if fw.isNodeType then (Some(nodeTypes(fw)), "")
      else if fw.isRelType then (Some(relTypes(fw)), "")
      else (None, s)
    else if rest1(0) != '(' then (None, s) else 
      val (param, rest2) = rest1.partitionByCharEscaped(')','"')
      val rest2afterParen = rest2.stripPrefix(")").trim
      val hasLinkDot = rest2afterParen.startsWith(".")
      val inner = param.trim.drop(1)
      val unquoted = inner.dropQuotes
      if fw == "Undefined" then
        if inner.isStrAttrType then (Some(Undefined(strAttrTypes(inner))), rest2afterParen) 
        if inner.isIntAttrType then (Some(Undefined(intAttrTypes(inner))), rest2afterParen) 
        else (None, s) 
      else if fw.isEntType then 
        if !hasLinkDot then
          (Some(entTypes(fw).apply(unquoted)), rest2afterParen)
        else 
          val relPart = rest2afterParen.drop(1)
          val relWord = relPart.firstWord
          val rest3 = relPart.drop(relWord.length)
          if relWord.isRelType then (Some(Link(entTypes(fw).apply(unquoted), relTypes(relWord))), rest3)
          else (None, s)
      else if fw.isStrAttrType then (Some(strAttrTypes(fw).apply(unquoted)), rest2afterParen)
      else if fw.isIntAttrType then 
        val intOpt = inner.toIntOption
        if intOpt.isDefined then (Some(intAttrTypes(fw).apply(intOpt.get)), rest2afterParen)
        else (None, s)
      else (None, s)

  def matrix: Seq[Seq[String]] = for Concept(n, d, t, g) <- concepts yield Seq(n, t, g, d) 

  def csv(delim: String = ";"): String = 
    s"concept${delim}type${delim}group${delim}description\n" + matrix.map(_.mkString(delim)).mkString("\n")

  def generate: String = 
    def entExtensions(name: String): String = 
      s"|  infix def $name(sub: Elem*): Rel = Rel(e, ${name.capitalize}, Model(sub*))\n" +
      s"|  infix def $name: Link = Link(e, ${name.capitalize})\n"

    s"""|//--- THIS IS A GENERATED FILE! DO NOT EDIT AS CHANGES ARE LOST ON RE-GENERATION 
        |//--- Edit the code below by changing `def generate` in reqt.meta
        |//--- Generate this file in sbt> `meta`
        |//--- or by `println(reqt.meta.generate)` in sbt console and copy-paste
        |
        |package reqt
        |
        |final case class Model(elems: Vector[Elem]) extends ModelMembers
        |case object Model extends ModelCompanion
        |
        |sealed trait Elem:
        |  def t: ElemType
        |  def isEnt: Boolean = this.isInstanceOf[Ent]
        |  def isRel: Boolean = this.isInstanceOf[Rel]
        |  def isNode: Boolean = this.isInstanceOf[Node]
        |  def isAttr: Boolean = this.isInstanceOf[Attr[?]]
        |  def isIntAttr: Boolean = this.isInstanceOf[IntAttr]
        |  def isStrAttr: Boolean = this.isInstanceOf[StrAttr]
        |  def isUndefined: Boolean = this.isInstanceOf[Undefined[?]]
        |
        |object Elem:
        |  given elemOrd: Ordering[Elem] = Ordering.by:
        |    case IntAttr(t, value) => (t.toString,   value, "",         "",         "")
        |    case StrAttr(t, value) => (t.toString,   0,     value,      "",         "")
        |    case Undefined(t) =>      ("Undefined",  0,     t.toString, "",         "")
        |    case Ent(t, id) =>        (t.toString,   0,     id,         "",         "") 
        |    case Rel(e, t, sub) =>    (e.t.toString, 1,     e.id,       t.toString, sub.showCompact)
        |
        |sealed trait Node extends Elem:
        |  def t: NodeType
        |
        |sealed trait ElemType
        |sealed trait NodeType extends ElemType
        |sealed trait AttrType[T] extends NodeType:
        |  def apply(value: T): Attr[T]
        |
        |final case class Link(e: Ent, t: RelType)
        |
        |final case class Ent private (t: EntType, id: String) extends Node
        |object Ent:
        |  val emptyId = "???"
        |  def apply(t: EntType, id: String): Ent = 
        |    new Ent(t, if id.isEmpty then emptyId else id)
        |
        |final case class LinkType(et: EntType, rt: RelType) 
        |
        |sealed trait Attr[T] extends Node:
        |  def t: AttrType[T]
        |  def value: T
        |
        |final case class StrAttr(t: StrAttrType, value: String) extends Attr[String]
        |case object StrAttr extends meta.ConceptGroup
        |
        |final case class IntAttr(t: IntAttrType, value: Int) extends Attr[Int], meta.ConceptGroup
        |case object IntAttr extends meta.ConceptGroup
        |
        |final case class Undefined[T](t: AttrType[T]) extends Attr[T]:
        |  def value: T = throw new java.util.NoSuchElementException
        |
        |final case class Rel(e: Ent, t: RelType, sub: Model) extends Elem:
        |  def link: Link = Link(e, t)
        |  def subnodes: Vector[Node] = sub.elems.collect{ case n: Node => n }
        |  def subrels: Vector[Rel] = sub.elems.collect{ case r: Rel => r }
        |  def expandSubnodes: Vector[Rel] = sub.elems.collect{ case n: Node => Rel(e, t, Model(n)) }
        |
        |enum EntType extends NodeType:
        |  def apply(id: String): Ent = Ent(this, id)
        |  case ${entityNames.mkString(", ")}
        |
        |enum StrAttrType extends AttrType[String]:
        |  def apply(value: String): StrAttr = StrAttr(this, value)
        |  case ${strAttrNames.mkString(", ")}
        |
        |enum IntAttrType extends AttrType[Int]:
        |  def apply(value: Int):    IntAttr = IntAttr(this, value)
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
        |extension (e: Ent)
        |""".stripMargin ++ relationNames.map(entExtensions).mkString("","\n", "").stripMargin
  end generate

  def graph(showElem: Boolean = true, showElemType: Boolean = true): String = 
    import GraphVizGen.*
    val (model, elem, node, rel, ent, attr, strAttr, intAttr) = 
        ("Model", "Elem", "Node_", "Rel", "Ent", "Attr", "StrAttr", "IntAttr")

    val (elemType, nodeType, relType, entType, attrType, strAttrType, intAttrType) =
        ("ElemType", "NodeType", "RelType", "EntType", "AttrType", "StrAttrType", "IntAttrType")

    val relTypeValues = 
      relationNames.map(_.capitalize).grouped(2).map(_.mkString(", ")).mkString("",",\\l","\\l")
    
    val entTypeValues = 
      entityNames.grouped(3).map(_.mkString(", ")).mkString("",",\\l","\\l")
    
    val strAttrTypeValues = 
      strAttrNames.grouped(3).map(_.mkString(", ")).mkString("",",\\l","\\l")
    
    val intAttrTypeValues = 
      intAttrNames.grouped(3).map(_.mkString(", ")).mkString("",",\\l","\\l")
      
    classDiagram("Metamodel")
      (rankSame = Seq(
        (if !showElem && !showElemType then Seq("Model") else Seq()) 
          ++ (if showElem then Seq("Model", "Elem") else Seq()) 
          ++ (if showElemType then Seq("ElemType") else Seq())) ++ 
        (if showElem then Seq(Seq("Node_", "Rel")) else Seq()) ++ 
        (if showElem then Seq(Seq("Ent", "Attr") ++ (if showElemType then Seq("AttrType") else Seq())) else Seq())
      )
      (edges = Seq() 
        ++ (if showElem then 
            Seq(node -> elem, rel -> elem, ent -> node, attr -> node, strAttr -> attr, intAttr -> attr) 
          else Seq())
        ++ (if showElemType then 
              Seq(nodeType -> elemType, relType -> elemType, entType -> nodeType, attrType -> nodeType, strAttrType -> attrType, intAttrType -> attrType) 
            else Seq())
      )
      (nodeFormats = (if !showElem && !showElemType then 
            Seq(recordNode(model)(model,"elems: Seq[Elem]")) 
          else Seq()) ++ 
        (if showElem then Seq(
          recordNode(model)(model,"elems: Seq[Elem]"),
          recordNode(elem) (elem,"t: ElemType"),
          recordNode(node) ("Node","t: NodeType"),
          recordNode(rel)  (rel, Seq("e: Ent","t: RelType","sub: Model").mkString("","\\l","\\l")),
          recordNode(ent)  (ent, Seq("t: EntType","id: String").mkString("","\\l","\\l")),
          recordNode(attr) ("Attr[T]", Seq("t: AttrType[T]","value: T").mkString("","\\l","\\l")),
          recordNode(strAttr) (strAttr, Seq("t: StrAttrType","value: String").mkString("","\\l","\\l")),
          recordNode(intAttr) (intAttr, Seq("t: IntAttrType","value: Int").mkString("","\\l","\\l")),
        ) else Seq()) ++ (if showElemType then Seq(
          recordNode(nodeType) (nodeType),
          recordNode(attrType) ("AttrType[T]"),
          recordNode(relType, fontSize = 9)  ("enum " + relType, relTypeValues),
          recordNode(entType, fontSize = 9)  ("enum " + entType, entTypeValues),
          recordNode(strAttrType, fontSize = 9)  ("enum " + strAttrType, strAttrTypeValues),
          recordNode(intAttrType, fontSize = 9)  ("enum " + intAttrType, intAttrTypeValues),
        ) else Seq()))

  end graph

  object QuickRef:
    val ending = "\\end{multicols*}\n\\end{document}"

    val preamble = 
      s"""|%!TEX encoding = UTF-8 Unicode
          |\\documentclass[a4paper,oneside]{article}
          |
          |\\usepackage[top=18mm,bottom=3mm, hmargin=10mm,landscape]{geometry}
          |
          |\\usepackage[utf8]{inputenc}
          |\\usepackage[T1]{fontenc}
          |
          |\\usepackage{tgtermes}
          |\\usepackage{lmodern}
          |\\usepackage[scaled=0.9]{beramono} % inconsolata or beramono ???
          |\\usepackage{microtype} % Slightly tweak font spacing for aesthetics
          |
          |\\usepackage{fancyhdr}
          |\\pagestyle{fancy}
          |\\chead{\\url{https://github.com/reqT/reqT-lang/blob/main/docs/reqT-quickref.tex}}
          |\\lhead{QuickRef reqT v4}
          |\\rhead{Compiled \\today}
          |
          |\\usepackage{hyperref}
          |\\hypersetup{colorlinks=true, linkcolor=blue, urlcolor=blue}
          |\\usepackage[usenames,dvipsnames,svgnames,table]{xcolor}
          |${LatexGen.defineColors}
          |\\usepackage{listings}
          |${LatexGen.lstDefineStyle}
          |\\lstset{style=reqT}
          |\\usepackage{multicol}
          |
          |\\setlength\\parindent{0em}
          |\\setlength\\headsep{1em}
          |\\setlength\\footskip{0em}
          |\\usepackage{titlesec}
          |  \\titlespacing{\\section}{0pt}{3pt}{2pt}
          |  \\titlespacing{\\subsection}{0pt}{3pt}{2pt}
          |  \\titlespacing{\\subsubsection}{0pt}{3pt}{2pt}
          |
          |\\usepackage{graphicx}
          |
          |\\pagenumbering{gobble}
          |
          |\\renewcommand{\\rmdefault}{\\sfdefault}
          |
          |\\begin{document}
          |""".stripMargin 

    def conceptDef(concept: String, definition: String): String = 
      s"\\hangindent=1em\\lstinline+$concept+ $definition \n"

    val body = 
      s"""|
          |\\fontsize{9.1}{11}\\selectfont
          |
          |\\begin{multicols*}{4}
          |
          |\\section*{The reqT v4 meta-model}
          |\\raggedright
          |A model is a sequence of elements. An element can be a node or a relation. A node can be an entity or an attribute. An entity has a type and an id. An attribute has a type and a value and it can be a string attribute or an integer attribute. A relation connects an entity to a sub-model via a relation type.
          |
          |\\noindent\\hspace*{-2.1em}\\includegraphics[width=8.2cm]{metamodel-Elem-GENERATED.pdf}
          |
          |\\section*{\\texttt{enum EntType}}
          |${
            val subsections = for eg <- EntGroup.values yield
              val head = s"\\subsection*{\\texttt{EntGroup.$eg}}"
              val es = entGroupTypesMap(eg).toSeq.map(_.toString).sorted
              val xs = es.map(e => conceptDef(e, conceptMap(e).descr)) 
              head + xs.mkString("\n", "\n", "\n")
            subsections.mkString("\n")
          }
          |
          |\\section*{\\texttt{enum relType}}
          |${
            val xs = for (s, c) <- relTypes yield
              conceptDef(s, conceptMap(s).descr)
            xs.mkString("\n")
            }
          |
          |\\section*{\\texttt{enum StrAttrType}}
          |${
            val xs = for (s, c) <- strAttrConcepts yield
              conceptDef(s, conceptMap(s).descr)
            xs.mkString("\n")
          }
          |
          |\\section*{\\texttt{enum IntAttrType}}
          |${
            val xs = for (s, c) <- intAttrConcepts yield
              conceptDef(s, conceptMap(s).descr)
            xs.mkString("\n")
          } 
          |
          |""".stripMargin

    val toLatex = preamble + body + ending

    