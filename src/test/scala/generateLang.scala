package reqt 

extension (s: String) def saveTo(filePath: String) = 
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
        |""".stripMargin

