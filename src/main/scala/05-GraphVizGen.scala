package reqt

object GraphVizGen:
  // --- BEGIN copied from reqT 3.1.7; to be refactored
  def formats = s"""|compound=true;overlap=false;rankdir=LR;clusterrank=local;
                    |node [fontname="Sans", fontsize=9];
                    |edge [fontname="Sans", fontsize=9];
                    |""".stripMargin
  def preamble: String = s"""digraph ${q}reqT.Model${q} { $nl$formats$nl"""
  def ending: String = "\n}"

  def style(elem: Elem): String = elem match
    case Ent(t, id) => s" [label=$q$t$nlLiteral$id$q, shape=box]"
    case Undefined(t) => s" [label=$q$t$nlLiteral ? $q, shape=box, style=rounded]"
    case StrAttr(t, value) =>  s" [label=$q$t$nlLiteral$value$q, shape=box, style=rounded]"
    case IntAttr(t, value) =>  s" [label=$q$t$nlLiteral$value$q, shape=box, style=rounded]"
    case Rel(e, t, sub) => ""
    
  def node(e: Elem, path: Path): String = s"  $q${path}/${e}$q"

  def singleSubnodeLink(from: Ent, rt: RelType, to: Elem, path: LinkPath): String =
    s"\n//singleSubnodeLink($from,$rt,$to,$path)\n\n"+
    indent(path.depth) + node(from, path) + style(from) + ";\n" +
    indent(path.depth) + node(to, path / Link(from,rt)) + style(to) + ";\n" +
    indent(path.depth) + node(from, path) + " -> " 
      + node(to, path/Link(from,rt)) + s" [label=$q${rt.show}  $q]" + ";\n"

  def subGraphPre(from: Ent, link: RelType, to: Elem, path: LinkPath): String =
    s"\n//subGraphPre($from,$link,$to,$path)\n\n"+ // add comment to help debug
    indent(path.depth) + node(from, path) + style(from) + ";\n" +
    indent(path.depth) + node(from, path) + " -> " + node(to, path/Link(from,link)) +
    s" [label=$q${link.show}  $q, labeljust=l, lhead=${q}cluster_$from$q]" + ";\n" +
    indent(path.depth) + s"  subgraph ${q}cluster_$from$q { \n"


  def nestedBody(m: Model, path: LinkPath = Path.Root): String =
    val xs: Seq[String] = m.elems.collect:
      case n: Node => indent(path.depth) + node(n, path) + style(n) +";\n"
      case Rel(e1,l1,sub) => sub.elems match
        case Vector() => indent(path.depth) + node(e1, path) + style(e1) +";\n"
        case Vector(e2) if e2.isNode => singleSubnodeLink(e1, l1, e2, path)
        case Vector(Rel(e2, _ , Model(Vector()))) => singleSubnodeLink(e1, l1, e2, path)
        case Vector(Rel(e2, l2, sub2)) if sub2.tip.size == 1 =>
          singleSubnodeLink(e1, l1, e2, path) +
          singleSubnodeLink(e2, l2, sub2.tip.elems.head, path/Link(e1,l1)) +
          nestedBody(sub2, path/Link(e1,l1)/Link(e2,l2))
        case _ =>
          subGraphPre(e1, l1, sub.tip.elems.head, path) +
          nestedBody(sub, path/Link(e1,l1))  + indent(path.depth + 1) + "}\n"
    xs.mkString

  def modelToGraphNested(m: Model): String = preamble + nestedBody(m) + ending


  // --- END


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