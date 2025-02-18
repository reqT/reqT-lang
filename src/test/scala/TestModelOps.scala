package reqt

class TestModelOps extends munit.FunSuite:

  test("Model tip top       "):
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
      Model(Prio(1),Req("x"),Prio(2),Undefined(Prio),Req("x")).updated(Prio(42)).distinctTopAttrType == 
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

  test("Model normal distinct   "):
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

    assert(m.normalize == n)
    assert(m.distinct == d)
  
    assert:
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Req("x"),Prio(3)).elems.distinct ==
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Prio(3)).elems

    assert:
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Req("x"),Prio(3)).distinctAttrTypeDeep ==
      Model(Prio(1),Req("x"),Req("y"),Req("x"))

  test("Model invariants        "):
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
    
    assert: 
      m.distinct == m.paths.map(_.toModel).reduceLeft(_ ++ _)
    // assert:   // AAARGH TODO think what is right here
    //   m.expand == m.paths.map(_.toModel).reduceLeft(_ :++ _)
    
    assert:
      m.prune.sorted == m.prune.normalize

    assert:
      m.paths.map(_.show).map(Path.fromString).map(_.get) == m.paths
      
    val ms = Seq.tabulate(100)(i => Model.random(10))

    assert: 
      ms.forall(m => m.paths.map(_.show).map(Path.fromString).map(_.get) == m.paths)

    assert: 
      ms.forall(m => m.prune.expand.normalize == m.expand.prune.normalize)
      
    assert: 
      ms.forall(m => m.prune.expand.normalize == m.expand.prune.normalize)


    // assert: 
    //   randomModels.forall(m => m.distinct == m.paths.map(_.toModel).foldLeft(Model())(_ ++ _))  ????

    // assert:
    //   m.maximal == m.paths.map(_.toModel).reduceLeft(_ :++ _)
    // assert:
    //   m.minimal.sorted == m.minimal.normal

      //TODO for all random models....

  test("Model ordering          "):
    val m = Model(
      Stakeholder("a"),
      Feature("x").has( 
        Stakeholder("b"),
        Req("a") has Order(2),
        Req("b") has Order(3),
        Req("c") has Order(4),
        Req("d") has Order(1),
        Req("e") has Order(7),
      ),
      Feature("y").has(
        Stakeholder("b"),
        Req("a") has Prio(2),
        Req("b") has Prio(3),
        Req("c") has Prio(4),
        Req("d") has Prio(1),
        Req("e") has Prio(7),
      ),
    )
    
    assert(m.sortLeafRelsBy(Order) == Vector(Req("d"), Req("a"), Req("b"), Req("c"), Req("e")))

    assert(m.sortLeafRelsBy(Order) == m.sortLeafRelsBy(Prio))
    assert(m.sortLeafRelsBy(Prio).toModel.withRank(Order).toModel.sortLeafRelsBy(Order) == m.sortLeafRelsBy(Order))

  test("Model Examples          "):
    import examples.Prioritization.*
    assert(normalizedVotes(DollarTest).intValues.sum == 99)



