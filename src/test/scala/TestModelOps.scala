package reqt

class TestModelOps extends munit.FunSuite:

  test("Model cut top tip       "):
    assert:
      Model(Prio(1), Req("x").has(Prio(1))).tip == Model(Prio(1), Req("x"))
    assert:
      Model(Prio(1), Req("x").has(Prio(1), Req("y").has(Prio(1)))).top == 
        Model(Prio(1), Req("x").has(Prio(1), Req("y")))

  test("Model add concat        "):
    assert:
      Model(Prio(1),Prio(2),Undefined(Prio)).updated(Prio(42)) == 
        Model(Prio(42), Prio(2),Undefined(Prio))

    assert:
      Model(Prio(1),Req("x"),Prio(2),Undefined(Prio),Req("x")).updated(Prio(42)).distinctAttrType == 
        Model(Prio(42),Req("x"),Req("x"))

    assert:
      Model(Req("x").has(Req("y")), Req("y").has(Req("b")), Req("x").has(Req("z")))
        .mergeFirst(Req("y").has(Req("a"))) ==
          Model(Req("x").has(Req("y")), Req("y").has(Req("b"), Req("a")), Req("x").has(Req("z")))

    val m = m""" 
          * Req 1 
            * Req 1.1
              * Req 1.1.1 has Prio 1
              * Req 1.1.2 has Prio 2
            * Req 1.2
              * Req 1.2.1
              * Req 1.2.2
          * 
          """.trim

    val m2 = m""" 
          * Req 1 
            * Req 1.1
              * Req 1.1.1 has Prio 1
              * Req 1.1.3 has Prio 2
            * Req 1.2
              * Req 1.2.1
              * Req 1.2.3
          * 
          """.trim
    
    assert((m ++ m) == m)

    assert(Model() ++ Model() == Model()) 

    assert(Model().append(Model()) == Model()) 
    
    assert( Model(Req("y"), Prio(1)) ++ Model(Req("y") has (Prio(1), Req("y"))) == 
            Model(Req("y"), Prio(1), Req("y") has (Prio(1), Req("y"))))

  test("Model append prepend    "):
    assert:
      Model() :+ Req("x") == Model(Req("x")) 
    assert:
      Model() :+ Prio(1) == Model(Prio(1)) 
    assert:
      Model() :+ Req("x").has(Prio(1)) == Model(Req("x").has(Prio(1))) 
    assert:
      Req("x") +: Model() == Model(Req("x")) 
    assert:
      Prio(1) +: Model() == Model(Prio(1)) 
    assert:
      Req("x").has(Prio(1)) +: Model() == Model(Req("x").has(Prio(1))) 

  test("Model normal distinct    "):
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
      Req("x").has(Prio(1),Prio(2),Req("z")),
      Req("x").has(Prio(1),Prio(2),Req("x")),
      Req("x").has(Prio(2),Prio(1),Req("y")),
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

    val d = Model(
      Req("y").has(Prio(1),Prio(2),Req("z").has(Req("a"),Req("b")),Req("x"),Req("y"),Req("z")),
      Prio(2),
      Prio(1),
      Undefined(Prio),
      Req("x"),
      Req("x").has(Prio(1),Prio(2),Req("z"),Req("x"),Req("y"))
    )

    assert(m.normal == n)
    assert(m.distinct == d)
  
    assert:
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Req("x"),Prio(3)).distinctElems ==
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Prio(3))

    assert:
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Req("x"),Prio(3)).distinctAttrType ==
      Model(Prio(1),Req("x"),Req("y"),Req("x"))

    assert: // invariants: all below should always hold
      m.distinct == m.paths.map(_.toModel).reduceLeft(_ ++ _)  
      m.maximal == m.paths.map(_.toModel).reduceLeft(_ :++ _)

      //TODO for all random models....

