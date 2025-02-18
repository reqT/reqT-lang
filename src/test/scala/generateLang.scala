package reqt 

extension (s: String)
  def saveTo(filePath: String) = 
    val pw = java.io.PrintWriter(java.io.File(filePath), "UTF-8")
    try pw.write(s) finally pw.close()

val modelFile = "src/main/scala/03-model-GENERATED.scala"
val docsDir = "docs/"
val targetDir = "target/"
val langSpecFile = docsDir + "/langSpec-GENERATED.md"
val conceptFile = docsDir + "/concepts-GENERATED.csv"
def graphFile(mod: String) = s"metamodel$mod-GENERATED.dot"
val quickRefTexFile = meta.QuickRef.fileName + ".tex"

@main def generateLang = 
  println(s"Generating $modelFile")
  meta.generate.saveTo(modelFile)
  
  java.io.File(docsDir).mkdirs()

  println(s"Generating $langSpecFile")
  langSpec.specMarkDown.saveTo(langSpecFile)
  showDeprecations()
  println(s"Generating $conceptFile")
  meta.csv("\t").saveTo(conceptFile)

  val gfs = IArray("-Model", "-Elem", "-ElemType", "-All").map(graphFile)
  println(s"Generating graph files: metamodel-*-GENERATED.dot")
  meta.graph(showElem=false, showElemType=false).saveTo(docsDir + gfs(0))
  meta.graph(showElem=true, showElemType=false) .saveTo(docsDir + gfs(1))
  meta.graph(showElem=false, showElemType=true) .saveTo(docsDir + gfs(2))
  meta.graph(showElem=true, showElemType=true)  .saveTo(docsDir + gfs(3))

  if isDotInstalled() then 
    import scala.sys.process._
    for f <- gfs do
      println(s"Generating pdf files in $targetDir for graph: $f")
      val q = if (isWindows) '"'.toString else ""
      val cmd =  
        Seq("dot",s"-Tpdf", "-o", s"$targetDir$q${newFileType(f, ".pdf")}$q",
      s"$q$docsDir$f$q")
      fixCmd(cmd).!

  println(s"Generating $quickRefTexFile in $targetDir")
  val latex = meta.QuickRef.toLatex
  latex.saveTo(targetDir + quickRefTexFile)
  latex.saveTo(docsDir + quickRefTexFile)

  println(os.proc("latexmk", "-pdf", "-silent", quickRefTexFile).call(cwd = os.pwd / targetDir.stripSuffix("/"))) 

object showDeprecations:
  def apply(isVisible: Boolean = true) = if isVisible then println(report)

  def report = 
    extension (xs: Seq[String]) 
      def p = xs.sorted.mkString(", ").wrap(72)

    s"""|
        |--++ CHANGES SINCE reqT ${old.version3}
        |  -- Deleted entities:   ${deletedEntities.size}    \n${deletedEntities.p}
        |  ++ Added entities:     ${addedEntities.size}      \n${addedEntities.p}
        |  -- Deleted attributes: ${deletedAttributes.size}  \n${deletedAttributes.p}
        |  ++ Added attributes:   ${addedAttributes.size}    \n${addedAttributes.p}
        |  -- Deleted relations:  ${deletedRelations.size}   \n${deletedRelations.p}
        |  ++ Added relations:    ${addedRelations.size}     \n${addedRelations.p}
        |
        | Migration Advice: replacement suggestions for changed elems since 3.1.7:
        |${old.migrationAdvice.mkString("\n")}
        |""".stripMargin

  def deletedEntities = old.entities.diff(meta.entityNames)
  def addedEntities   = meta.entityNames.diff(old.entities)

  def deletedAttributes = old.attributes.diff(meta.intAttrNames ++ meta.strAttrNames)
  def addedAttributes   = (meta.intAttrNames ++ meta.strAttrNames).diff(old.attributes)

  def deletedRelations = old.relations.diff(reqt.meta.relationNames)
  def addedRelations   = meta.relationNames.diff(old.relations)

  object old:
    val version3 = "3.1.7"

    val entities = Vector("Term", "Barrier", "Member", "Target", "Function", "MockUp", "Interface", "Event", "Scenario", "Release", "App", "Configuration", "UseCase", "Req", "Ticket", "Goal", "Variant", "Meta", "Story", "Section", "Resource", "Quality", "Service", "Actor", "System", "Label", "VariationPoint", "Component", "Design", "User", "Breakpoint", "Test", "Relationship", "Item", "Issue", "WorkPackage", "Class", "Product", "State", "Epic", "Screen", "Feature", "Data", "Domain", "Risk", "Stakeholder", "Task", "Module", "Idea")

    val attributes = Vector("Min", "Prio", "Title", "Probability", "Code", "Why", "Profit", "Constraints", "Expectation", "Max", "Value", "Benefit", "Gist", "Input", "Output", "FileName", "Example", "Text", "Deprecated", "Order", "Cost", "Image", "Spec", "Comment", "Damage", "Capacity", "Frequency", "Status")

    val relations = Vector("is", "precedes", "deprecates", "superOf", "implements", "hurts", "has", "impacts", "verifies", "relatesTo", "excludes", "helps", "binds", "requires", "interactsWith")

    val concepts = (entities ++ attributes ++ relations).sorted

    val migration: Seq[((String, String, String), String)] = Seq(
      ("Deleted", "Entity", "Meta")        -> "use Data or Class",
      ("Deleted", "Entity", "MockUp")      -> "use Prototype",  
      ("Deleted", "Entity", "Ticket")      -> "use Issue",
      ("Added", "Entity", "Field") -> "more specific alternative to member", 
      ("Added", "Entity", "Image") -> "was attribute, use with attribute Location", 
      ("Added", "Entity", "Prototype") -> "instead of Mockup", 
      ("Deleted", "Attribute", "Code") -> "use Text with markdown code fences",
      ("Deleted", "Attribute", "FileName") -> "use Location",
      ("Deleted", "Attribute", "Image") -> "use entity Image with attribute Location",
      ("Deleted", "Attribute", "Status")      -> "use Label",
      ("Added", "Attribute", "Failure")  -> "use together with Risk, Test",
      ("Added", "Attribute", "Location") -> "instead of FileName",
      ("Deleted", "Relation", "superOf")       -> "use is in reverse direction",
    )

    def migrationAdvice = for (key, advice) <- migration yield 
      val (o, t, c) = key
      s"${o.padTo(8, ' ')} ${t.padTo(10, ' ')} ${c.padTo(14, ' ')} $advice" 
    
    extension (term: String) def oldDef: (String, String) = termTypeDefMap(term)

    val termTypeDefMap: Map[String, (String, String)] = 
      val xs = 
        for Seq(tpe, term, defi) <- typeTermDefSeq 
        yield (term, (tpe, defi))
      xs.toMap

    lazy val typeTermDefSeq: Seq[Seq[String]] = s"""
      |Entity;Actor;A human or machine that communicates with a system.
      |Entity;App;A computer program, or group of programs designed for end users, normally with a graphical user interface. Short for application.
      |Entity;Barrier;Something that makes it difficult to achieve a goal or a higher quality level.
      |Entity;Breakpoint;A point of change. An important aspect of a (non-linear) relation between quality and benefit.
      |Entity;Class;An extensible template for creating objects. A set of objects with certain attributes in common. A category.
      |Entity;Component;A composable part of a system. A reusable, interchangeable system unit or functionality.
      |Entity;Configuration;A specific combination of variants.
      |Entity;Data;Information stored in a system.
      |Entity;Design;A specific realization or high-level implementation description (of a system part).
      |Entity;Domain;The application area of a product with its surrounding entities.
      |Entity;Epic;A large user story or a collection of stories.
      |Entity;Event;Something that can happen in the domain and/or in the system.
      |Entity;Feature;A releasable characteristic of a product. A (high-level, coherent) bundle of requirements.
      |Entity;Function;A description of how input data is mapped to output data. A capability of a system to do something specific.
      |Entity;Goal;An intention of a stakeholder or desired system property.
      |Entity;Idea;A concept or thought (potentially interesting).
      |Entity;Interface;A defined way to interact with a system.
      |Entity;Issue;Something needed to be fixed.
      |Entity;Item; An article in a collection, enumeration, or series.
      |Entity;Label;A descriptive name used to identify something.
      |Entity;Member;An entity that is part of another entity, eg. a field in a in a class.
      |Entity;Meta;A prefix used on a concept to mean beyond or about its own concept, e.g. metadata is data about data.
      |Entity;MockUp;A prototype with limited functionality used to demonstrate a design idea.
      |Entity;Module;A collection of coherent functions and interfaces.
      |Entity;Product;Something offered to a market.
      |Entity;Quality;A distinguishing characteristic or degree of goodness.
      |Entity;Relationship;A specific way that entities are connected.
      |Entity;Release;A specific version of a system offered at a specific time to end users.
      |Entity;Req;Something needed or wanted. An abstract term denoting any type of information relevant to the (specification of) intentions behind system development. Short for requirement.
      |Entity;Resource;A capability of, or support for development.
      |Entity;Risk;Something negative that may happen.
      |Entity;Scenario;A (vivid) description of a (possible future) system usage.
      |Entity;Screen;A design of (a part of) a user interface.
      |Entity;Section;A part of a (requirements) document.
      |Entity;Service;Actions performed by systems and/or humans to provide results to stakeholders.
      |Entity;Stakeholder;Someone with a stake in the system development or usage.
      |Entity;State;A mode or condition of something in the domain and/or in the system. A configuration of data.
      |Entity;Story;A short description of what a user does or needs. Short for user story.
      |Entity;System;A set of interacting software and/or hardware components.
      |Entity;Target;A desired quality level or goal .
      |Entity;Task;A piece of work (that users do, maybe supported by a system).
      |Entity;Term;A word or group of words having a particular meaning.
      |Entity;Test;A procedure to check if requirements are met.
      |Entity;Ticket;(Development) work awaiting to be completed.
      |Entity;UseCase;A list of steps defining interactions between actors and a system to achieve a goal.
      |Entity;User;A human interacting with a system.
      |Entity;Variant;An object or system property that can be chosen from a set of options.
      |Entity;VariationPoint;An opportunity of choice among variants.
      |Entity;WorkPackage;A collection of (development) work tasks.
      |Attribute;Benefit;A characterisation of a good or helpful result or effect (e.g. of a feature).
      |Attribute;Capacity;The largest amount that can be held or contained (e.g. by a resource).
      |Attribute;Code;A collection of (textual) computer instructions in some programming language, e.g. Scala. Short for source code.
      |Attribute;Comment;A note that explains or discusses some entity.
      |Attribute;Constraints;A collection of propositions that restrict the possible values of a set of variables.
      |Attribute;Cost;The expenditure of something, such as time or effort, necessary for the implementation of an entity.
      |Attribute;Damage;A characterisation of the negative consequences if some entity (e.g. a risk) occurs.
      |Attribute;Deprecated;A description of why an entity should be avoided, often because it is superseded by another entity, as indicated by a 'deprecates' relation.
      |Attribute;Example;A note that illustrates some entity by a  typical instance.
      |Attribute;Expectation;The required output of a test in order to be counted as passed.
      |Attribute;FileName;The name of a storage of serialized, persistent data.
      |Attribute;Frequency;The rate of occurrence of some entity. 
      |Attribute;Gist;A short and simple description of an entity, e.g. a function or a test.
      |Attribute;Image;(The name of) a picture of an entity.
      |Attribute;Input;Data consumed by an entity, 
      |Attribute;Max;The maximum estimated or assigned (relative) value.
      |Attribute;Min;The minimum estimated or assigned (relative) value.
      |Attribute;Order;The ordinal number of an entity (1st, 2nd, ...).
      |Attribute;Output;Data produced by an entity, e.g. a function or a test.
      |Attribute;Prio;The level of importance of an entity. Short for priority.
      |Attribute;Probability;The likelihood that something (e.g. a risk) occurs.
      |Attribute;Profit;The gain or return of some entity, e.g. in monetary terms.
      |Attribute;Spec;A (detailed) definition of an entity. Short for specification
      |Attribute;Status;A level of refinement of an entity (e.g. a feature) in the development process. 
      |Attribute;Text;A sequence of words (in natural language).
      |Attribute;Title;A general or descriptive heading.
      |Attribute;Value;An amount. An estimate of worth.
      |Attribute;Why;A description of intention. Rationale.
      |Relation;binds;Ties a value to an option. A configuration binds a variation point.
      |Relation;deprecates;Makes outdated. An entity deprecates (supersedes) another entity.
      |Relation;excludes;Prevents a combination. An entity excludes another entity.
      |Relation;has;Expresses containment, substructure. An entity contains another entity.
      |Relation;helps;Positive influence. A goal helps to fulfil another goal.
      |Relation;hurts;Negative influence. A goal hinders another goal.
      |Relation;impacts;Some influence. A new feature impacts an existing component.
      |Relation;implements;Realisation of. A module implements a feature.
      |Relation;interactsWith;Communication. A user interacts with an interface.
      |Relation;is;Sub-typing, specialization, part of another, more general entity.
      |Relation;precedes;Temporal ordering. A feature precedes (is implemented before) another feature.
      |Relation;relatesTo;General relation. An entity is related to another entity.
      |Relation;requires;Requested combination. An entity is required (or wished) by another entity.
      |Relation;superOf;Super-typing, generalization, includes another, more specific entity.
      |Relation;verifies;Gives evidence of correctness. A test verifies the implementation of a feature.
      """.trim.stripMargin.split("\n").map(_.split(";").toSeq).toSeq

    

