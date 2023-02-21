class TestModelOps extends munit.FunSuite:
  import Console.{RED as R, RESET as X}
  import reqt.*

  test("Concat empty models        "):
    assert(Model() ++ Model() == Model()) 

  test("Concat non-empty models    "):
    assert( Model(Req("y"), Prio(1)) ++ Model(Req("y") has (Prio(1), Req("y"))) == 
            Model(Req("y"), Prio(1), Req("y") has (Prio(1), Req("y"))))

  test("Add entity to empty model  "):
    assert(Model() + Req("test") == Model(Req("test"))) 