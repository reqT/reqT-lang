package reqt

object GraphVizGen:

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

  def directedGraph(
    name: String, 
    fontName: String = "Sans",
    fontSize: Int = 10,
    rankDir: String = "BT",
    ordering: String = "out",
    noJustify: Boolean = true,
    edgeArrowhead: String = "empty",
    nodeShape: String = "record",
  )(rankSame: Seq[String]*)(edges: (String, String)*)(nodeFormats: String*) = 
    s"""|digraph $name {
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