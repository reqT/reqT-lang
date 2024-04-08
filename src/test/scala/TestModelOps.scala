package reqt

class TestModelOps extends munit.FunSuite:

  test("Concat empty models        "):
    assert(Model() ++ Model() == Model()) 

  test("Concat non-empty models    "):
    assert( Model(Req("y"), Prio(1)) ++ Model(Req("y") has (Prio(1), Req("y"))) == 
            Model(Req("y"), Prio(1), Req("y") has (Prio(1), Req("y"))))

  test("Add entity to empty model  "):
    assert(Model() + Req("test") == Model(Req("test"))) 

  test("Model normalization        "):
    val m = Model(
      Req("y").has(Prio(1),Prio(2),Req("z").has(Req("a"))),
      Req("y").has(Prio(1),Prio(2),Req("z").has(Req("b"))),
      Prio(2),
      Prio(1),
      Undefined(Prio),
      Undefined(Prio),
      Req("x"),
      Req("x").has(),
      Req("x"),
      Req("x").has(),
      Req("x").has(Prio(1),Prio(2),Req("x")),
      Req("x").has(Prio(1),Prio(2),Req("y")),
      Req("x").has(Prio(2),Prio(1),Req("z")),
      Req("y").has(Prio(1),Prio(2),Req("x")),
      Req("y").has(Prio(1),Prio(2),Req("y")),
      Req("y").has(Prio(1),Prio(2),Req("z")),
    )
    val n = Model(
      Prio(1),
      Prio(2),
      Req("x"),
      Req("x").has(Prio(1),Prio(2),Req("x"),Req("y"),Req("z")),
      Req("y").has(Prio(1),Prio(2),Req("x"),Req("y"),Req("z"),Req("z").has(Req("a"),Req("b"))),
      Undefined(Prio),
    )

    assert(m.normal == n)