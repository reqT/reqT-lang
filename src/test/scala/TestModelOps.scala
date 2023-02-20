class TestModelOps extends munit.FunSuite:
  import Console.{RED as R, RESET as X}
  import reqt.*

  test("Concat empty models   "):
    assert(Model() ++ Model() == Model()) 