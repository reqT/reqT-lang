package reqt

class TestParse extends munit.FunSuite {
  
  def parse(xs: (String, Model)*): Unit = 
    for (s, expected) <- xs do 
      val parsed = parser.parseModel(s)
      assert(parsed == expected, s">>> Falied on: $s\nParsed:  $parsed\nExpected:$expected")

  test("Simple StrAttr   "){ parse("Spec  x" -> Model(Spec("x"))) }

  test("2 Simple StrAttr "){ parse("Spec  x\nSpec y" -> Model(Spec("x"),Spec("y"))) }

  test("Multiline StrAttr"){ parse("Spec  xxx\n yyy" -> Model(Spec("xxx\n yyy"))) }

  test("Simple Non-elem  "){ parse("xxx\n yyy" -> Model(Text("xxx\n yyy"))) }

  test("Illegal IntAttr  "){ parse("Prio x y z" -> 
    Model(Err("??? Integer expected after Prio on line 0: Prio x y z"))) }

  test("Simple IntAttr   "){ parse("Prio 1" -> Model(Prio(1))) }

  test("IntAttr + space  "){ parse("Prio 1   " -> Model(Prio(1))) }

  test("IntAttr + extra  "){ parse("Prio 1 x y " -> Model(Prio(1), Err("??? x y"))) }

  test("Simple Ent       "){ parse("Feature  xxx" -> Model(Feature("xxx"))) }

  test("Simple Ent +extra"){ parse("Feature  xxx  hej " -> Model(Feature("xxx"), Err("??? hej"))) }

  test("Empty single Rel "){ parse("Feature x has" -> Model(Feature("x").has()))}

  test("Rel sub1         "){ parse("Feature x has\n Prio 1" -> Model(Feature("x").has(Prio(1))))}
  
  test("Rel sub2 outdent "): 
    parse:
      "Feature x has\n Prio 1\n Req y\nReq z" -> 
        Model(Feature("x").has(Prio(1),Req("y")),Req("z"))

  test("Illegal id in Rel"): 
    parse:
      "Feature x y has\n Prio 1\n  Req y\nReq z" -> Model(
        Feature("x") has (Prio(1),Req("y")),
        Err("??? Illegal multi-word id x y on line 0: Feature x y has"),
        Req("z"))

  test("Missing id + more"): 
    parse:
      "Feature has\n Prio 1\n  Req y\nReq z" -> 
        Model(
          Err("??? Missing id after Feature on line 0: Feature has"),
          Prio(1),Req("y"),Req("z"),
        )

  test("Missing id       "): 
    parse:
      "Feature\n Prio 1\n  Req y\nReq z" -> 
        Model(
          Err("??? Missing id after Feature on line 0: Feature"),
          Prio(1),Req("y"),Req("z"),
        )

  test("Bad indent       "):
    parse:
      s"""|
          |Feature x
          |  Prio 1
          |   Req y
          |Req z
          |
          |""".stripMargin -> 
        Model(
          Feature("x"),
          Err("??? bad indent on line 2, missing relation type after: Feature x"),
          Prio(1),Req("y"),Req("z"))

}