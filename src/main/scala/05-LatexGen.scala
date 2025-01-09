package reqt

object LatexGen:
  def defineColors: String = 
    s"""|\\definecolor{entityColor}{RGB}{0,100,200}
        |\\definecolor{attributeColor}{RGB}{0,100,50}
        |\\definecolor{relationColor}{RGB}{160,0,30}
        |""".stripMargin
  
  def lstDefineStyle: String = 
    s"""|\\lstdefinestyle{reqT}{
        |  %belowcaptionskip=1\\baselineskip,
        |  breaklines=true,
        |  %showstringspaces=false,
        |  showspaces=false,
        |  %breakatwhitespace=true,
        |  basicstyle=\\ttfamily\\small,
        |  emph={${meta.entityNames.mkString{","}}},
        |  emphstyle=\\bfseries\\color{entityColor},
        |  emph={[2]${meta.relationNames.mkString{","}}},
        |  emphstyle={[2]\\bfseries\\color{relationColor}},
        |  emph={[3]${meta.attributeNames.mkString{","}}},
        |  emphstyle={[3]\\bfseries\\color{attributeColor}},  
        |}
        |""".stripMargin
