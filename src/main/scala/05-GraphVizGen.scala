package reqt

object GraphVizGen:
  case class GraphVizSettings(
    fontName: String = "Sans",
    fontSize: Int = 10,
    rankDir: String = "LR",
    ordering: String = "out",
    noJustify: Boolean = true,
    edgeArrowhead: String = "empty",
    nodeShape: String = "record",
    compound: Boolean = true,
  )
  object GraphVizSettings:
    given default: GraphVizSettings = GraphVizSettings()

  extension (m: Model)
    def toGraphViz(using settings: GraphVizSettings): String = 
      val s = settings.copy(rankDir = "LR")
      ???

  def recordNode(
    nodeName: String,
    fontSize: Int = 10, 
    isHtml: Boolean = false,
  )(labelParts: String*): String =
    if isHtml then 
      s"""  $nodeName [ label = <{${labelParts.mkString("|")}}> fontsize = $fontSize]"""
    else
      s"""  $nodeName [ label = "{${labelParts.mkString("|")}}" fontsize = $fontSize]"""

  def edge(fromNode: String, toNode: String): String = s"  $fromNode -> $toNode"

  def classDiagram(title: String)
    (rankSame: Seq[Seq[String]])(edges: Seq[(String, String)])(nodeFormats: Seq[String]) 
    (using settings: GraphVizSettings) =
      val s = settings.copy(rankDir = "BT")
      import s.*
      s"""|digraph $title {
          |  fontname = "$fontName"
          |  fontsize = $fontSize
          |  rankdir =  "$rankDir"
          |  ordering = "$ordering"
          |  nojustify = $noJustify
          |
          |  node [
          |    fontname = "$fontName"
          |    fontsize = $fontSize
          |    shape = "$nodeShape"
          |  ]
          |
          |  edge [
          |    arrowhead = "$edgeArrowhead"
          |  ]
          |
          |${rankSame.map(_.mkString("  { rank = same; ", "; ", "; }")).mkString("  ", "\n  ", "\n")}
          |
          |${nodeFormats.mkString("  ", "\n  ", "\n")}
          |
          |${edges.map((f,t) => edge(f,t)).mkString("  ", "\n  ", "\n")}
          |}
          |""".stripMargin