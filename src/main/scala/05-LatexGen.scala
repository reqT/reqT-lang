package reqt

object LatexGen:
  def defineColors: String = 
    s"""|\\definecolor{entityColor}{RGB}{0,100,200}
        |\\definecolor{attributeColor}{RGB}{0,100,50}
        |\\definecolor{relationColor}{RGB}{160,0,30}
        |""".stripMargin
  
  def lstDefineStyle: String = 
    import meta.{entityNames, relationNames, attributeNames}
    s"""|\\lstdefinestyle{reqT}{
        |  %belowcaptionskip=1\\baselineskip,
        |  breaklines=true,
        |  %showstringspaces=false,
        |  showspaces=false,
        |  %breakatwhitespace=true,
        |  basicstyle=\\ttfamily\\small,
        |  emph={${entityNames.mkString{","}}},
        |  emphstyle=\\bfseries\\color{entityColor},
        |  emph={[2]${(relationNames ++ relationNames.map(_.capitalize)).mkString{","}}},
        |  emphstyle={[2]\\bfseries\\color{relationColor}},
        |  emph={[3]${attributeNames.mkString{","}}},
        |  emphstyle={[3]\\bfseries\\color{attributeColor}},  
        |}
        |""".stripMargin
