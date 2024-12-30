package reqt

object HtmlGen:
  def defaultStyle = 
    s"""|html{overflow-y:scroll;}
        |
        |body{
        |  font-size:15px;
        |  color:#000000;
        |  background-color:#f0f0f0;
        |  margin:15px;
        |}
        |
        |body,p,h1,h2,h3,h4,table,td,th,ul,ol,textarea,input{
        |  font-family:verdana,helvetica,arial,sans-serif;
        |}
        |
        |
        |h1,h2,h3,h4,h5,h6{
        |  background-color:transparent;
        |  color:rgb(10,10,10);
        |  font-weight:bold;
        |}
        |
        |h1 {font-size:190%;margin-top:0px;}
        |h2 {font-size:160%;margin-top:10px;margin-bottom:10px;}
        |h3 {font-size:120%;font-weight:normal;margin-top:4px;margin-bottom:4px;}
        |h4 {font-size:100%;}
        |h5 {font-size:90%;}
        |h6 {font-size:80%;}
        |
        |ul {
        |  list-style-position: outside;
        |  padding-inline-start: 1em;
        |
        |}
        |p {text-indent: 3em;}
        |
        |span.entityColor      {color:rgb(0,100,200);font-weight:bold;}
        |span.attributeColor {color:rgb(0,100,50);font-weight:bold;}
        |span.relationColor   {color:rgb(160,0,30);font-weight:bold;}
        |
        |table.nodelist{
        |  text-align:left;
        |  background-color:#ffffff;
        |  padding:4px;
        |  border:1px solid #d4d4d4;
        |  width:100%;
        |  margin-bottom:4px;
        |}
        |
        |td.name{width:100px;font-style:italic;}""".stripMargin

  case class HtmlSettings(title: String = "Untitled", style: String = defaultStyle)
  object HtmlSettings:
    given default: HtmlSettings = HtmlSettings()
  end HtmlSettings

  def preamble(m: Model)(using hs: HtmlSettings): String = 
    val title = (m / Title).headOption.getOrElse(hs.title)
    s"""|<!DOCTYPE html>
        |<html>
        |<head>
        |<title>$title</title>
        |${if hs.style.isEmpty then "" else s"<style>\n${hs.style}\n</style>"}
        |</head>""".stripMargin

  type HtmlCtx = HtmlSettings ?=> String

  def elemListItem(level: Int, cls: String = "")(et: ElemType)(after: String = ""): HtmlCtx = 
    ("  " * level) + s"""<li><span class="$cls"> $et</span>: $after </li>""" 

  def renderHtmlBody(m: Model, level: Int): HtmlCtx = 
    val ind = ("  " * level)
    val lines: Vector[String] = m.elems.map: 
      case Ent(t, id)        => elemListItem(level, "entityColor"   )(t)(id) 
      case StrAttr(t, value) => elemListItem(level, "attributeColor")(t)(value)
      case IntAttr(t, value) => elemListItem(level, "attributeColor")(t)(value.toString)
      case Undefined(t)      => elemListItem(level, "attributeColor")(t)("???")
      case Rel(e, t, sub)    => 
          s"""$ind<li><span class="entityColor"> ${e.t}</span>: ${e.id}""" 
          + s"""<span class="relationColor"> ${t.show}</span></li>\n"""
          + (if sub.elems.nonEmpty then renderHtmlBody(sub, level + 1) else "")
    lines.mkString(s"$ind<ul>\n","\n",s"\n$ind</ul>")

  extension (m: Model) 
    def toHtmlBody: HtmlCtx = 
      if m.elems.isEmpty then "<ul><li>Empty Model</li></ul>"
      else renderHtmlBody(m, 0)
    def toHtml: HtmlCtx = s"${preamble(m)}\n<body>\n${m.toHtmlBody}\n</body>\n</html>\n"
  
end HtmlGen