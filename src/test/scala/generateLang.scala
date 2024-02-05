package reqt 

extension (s: String)
  def saveTo(filePath: String) = 
    val pw = java.io.PrintWriter(java.io.File(filePath), "UTF-8")
    try pw.write(s) finally pw.close()

val modelFile = "src/main/scala/03-model-GENERATED.scala"
val langSpecFile = "langSpec-GENERATED.md"

@main def generateLang = 
  println(s"Generating $modelFile")
  reqt.meta.generate.saveTo(modelFile)
  println(s"Generating $langSpecFile")
  reqt.langSpec.specMarkDown.saveTo(langSpecFile)
  showDeprecations(true)

object showDeprecations:
  def apply(isVisible: Boolean) = if isVisible then println(report)

  object old:
    val entities = Vector("Term", "Barrier", "Member", "Target", "Function", "MockUp", "Interface", "Event", "Scenario", "Release", "App", "Configuration", "UseCase", "Req", "Ticket", "Goal", "Variant", "Meta", "Story", "Section", "Resource", "Quality", "Service", "Actor", "System", "Label", "VariationPoint", "Component", "Design", "User", "Breakpoint", "Test", "Relationship", "Item", "Issue", "WorkPackage", "Class", "Product", "State", "Epic", "Screen", "Feature", "Data", "Domain", "Risk", "Stakeholder", "Task", "Module", "Idea")

    val attributes = Vector("Min", "Prio", "Title", "Probability", "Code", "Why", "Profit", "Constraints", "Expectation", "Max", "Value", "Benefit", "Gist", "Input", "Output", "FileName", "Example", "Text", "Deprecated", "Order", "Cost", "Image", "Spec", "Comment", "Damage", "Capacity", "Frequency", "Status")

    val relations = Vector("is", "precedes", "deprecates", "superOf", "implements", "hurts", "has", "impacts", "verifies", "relatesTo", "excludes", "helps", "binds", "requires", "interactsWith")
    
    val version = "3.1.7"

    def migrationAdvice = """
      -- Deleted entities: 
        Actor (use System or User), App (use Product or System), Class (use DataType), Data (use DataType), Domain (use Section), Epic (use Section), Item (use Text or Label), Member (use Field or Function), Meta (use Type), MockUp (use Prototype), Module (use Component), Scenario (use UseCase or UserStory), Service (use Function or Feature), Story (use UserStory), Term (use Text or Label), Test (use TestCase), Ticket (use Issue), WorkPackage (use Section or Issue)
      ++ Added entities:
        DataType, Field, Image, Prototype, TestCase, UserStory
      -- Deleted attributes: 
        Code (use Text and markdown code fences), Constraints, FileName (use Location), Gist (use Spec or Text), Image (use entity Image and Location), Status (use Label), Title (use Heading with # indicating level h1 to h6)
      ++ Added attributes:
        Failure (use together with Risk or TestCase), Label (use instead of Item), Location (use to provide file name, URL etc)
      -- Deleted relations: 
        impacts (use relatesTo), interactsWith (use relatesTo or has), is (use supertypeOf in reverse order), superOf (use supertypeOf)
      ++ Added relations:
        supertypeOf
    """
  end old 

  def report = 
    extension (xs: Seq[String]) 
      def p = xs.sorted.mkString(", ").wrap(72)

    s"""|
        |--++ CHANGES SINCE reqT ${old.version}
        |  -- Deleted entities: 
        |${old.entities.diff(meta.entityNames).p}
        |  ++ Added entities:
        |${meta.entityNames.diff(old.entities).p}
        |  -- Deleted attributes: 
        |${old.attributes.diff(meta.intAttrNames ++ meta.strAttrNames).p}
        |  ++ Added attributes:
        |${(meta.intAttrNames ++ meta.strAttrNames).diff(old.attributes).p}
        |  -- Deleted relations: 
        |${old.relations.diff(reqt.meta.relationNames).p}
        |  ++ Added relations:
        |${reqt.meta.relationNames.diff(old.relations).p}
        |
        | Migration Advice: replacement suggestions for changed elems in reqt 3.1.7:
        | ${old.migrationAdvice}
        |""".stripMargin

