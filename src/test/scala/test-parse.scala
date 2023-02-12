package reqt

class TestParse extends munit.FunSuite {
  
  def parse(xs: (String, Model)*): Unit = 
    for (s, expected) <- xs do 
      val parsed = ModelParser.parse(s)
      assert(parsed == expected, s">>> Falied on: $s\nParsed:$parsed\nExpected:$expected")

  test("Simple StrAttr   "){ parse("Spec  x" -> Model(Spec("x"))) }
  test("2 Simple StrAttr "){ parse("Spec  x\nSpec y" -> Model(Spec("x"),Spec("y"))) }
  test("Multiline StrAttr"){ parse("Spec  xxx\n yyy" -> Model(Spec("xxx\n yyy"))) }
  test("Simple Non-elem  "){ parse("xxx\n yyy" -> Model(Text("xxx\n yyy"))) }
  test("Illegal IntAttr  "){ parse("Prio x y z" -> Model(Text("??? Prio x y z"))) }
  test("Simple IntAttr   "){ parse("Prio 1" -> Model(Prio(1))) }
  test("IntAttr + space  "){ parse("Prio 1   " -> Model(Prio(1))) }
  test("IntAttr + extra  "){ parse("Prio 1 x y " -> Model(Prio(1), Text("x y"))) }
  test("Simple Ent       "){ parse("Feature  xxx" -> Model(Feature("xxx"))) }
  test("Simple Ent +extra"){ parse("Feature  xxx  hej " -> Model(Feature("xxx"), Text("??? hej"))) }

}