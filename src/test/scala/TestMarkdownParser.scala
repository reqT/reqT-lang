package reqt

class TestMarkdownParser extends munit.FunSuite:
  import Console.{RED as R, RESET as X}

  test("Simple StrAttr   "):
    assert("* Spec  x x x \n y".toModel == Model(Spec("x x x\n y"))) 

  test("2 Simple StrAttr "):
    assert("* Spec  x\n* Spec y".toModel == Model(Spec("x"),Spec("y"))) 

  test("Multiline StrAttr"):
    assert("* Spec  x x x\n y y y".toModel == Model(Spec("x x x\n y y y"))) 

  test("Simple Non-elem  "):
    assert("xxx\n yyy".toModel == Model(Text("xxx\n yyy"))) 

  test("Illegal IntAttr  "):
    assert("* Prio x y z".toModel == Model(Undefined(Prio), Text("x y z"))) 

  test("Simple IntAttr   "):
    assert("* Prio 1".toModel == Model(Prio(1))) 

  test("IntAttr + space  "):
    assert("* Prio 1   ".toModel == Model(Prio(1))) 

  test("IntAttr + extra  "):
    assert("* Prio 1 x y ".toModel == Model(Prio(1), Text("x y"))) 

  test("Simple Ent       "):
    assert("* Feature  x ".toModel == Model(Feature("x"))) 
    assert("* Feature  x ".toModel.toMarkdown == Model(Feature("x")).toMarkdown) 

  test("Simple Ent +extra"):
    assert("* Feature  x y ".toModel == Model(Feature("x y"))) 

  test("Empty single Rel "):
    assert("* Feature x has".toModel == Model(Feature("x").has()))

  test("Rel sub1         "): 
    assert("* Feature x has\n * Prio 1".toModel == Model(Feature("x").has(Prio(1))))
  
  test("Rel sub2 outdent "): 
    assert:
      "* Feature x has\n * Prio 1\n * Req y\n* Req z".toModel == 
        Model(Feature("x").has(Prio(1),Req("y")),Req("z"))

  test("Illegal id in Rel"): 
    assert:
      "* Feature x y has\n * Prio 1\n  * Req y\n* Req z".toModel == 
        Model(Feature("x y") has (Prio(1),Req("y")), Req("z"))

  test("Missing id + more"): 
    assert:
      "* Feature    has\n * Prio 1\n  * Req y\n* Req z".toModel == 
        Model(
          Feature("???") has(Prio(1),Req("y")),
          Req("z")
        )

  test("Missing id       "): 
    assert:
      "* Feature\n * Prio 1\n* Req y\n* Req z".toModel == 
        Model(
          Feature("???") has Prio(1),
          Req("y"),Req("z")
        )

  test("Colon-insensitive"):
    assert:
      "* Class: user: has: Spec: hej:".toModel ==
        Model(
          Class("user").has(
            Spec("hej:"),
          ),
        )

  test("Accept bad indent"):
    assert:
      s"""|
          |* Feature x
          |  * Prio 1
          |         * Req y
          |* Req z
          |
          |""".stripMargin.toModel == 
        Model(
          Text(""),
          Feature("x") has (Prio(1),Req("y")),
          Req("z")
        )
