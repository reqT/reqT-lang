package reqt

class TestModelOps extends munit.FunSuite:

  test("Model tip top       "):
    assert:
      Model(Prio(1), Req("x").has(Prio(1))).tip == Model(Prio(1), Req("x"))
    assert:
      Model(Prio(1), Req("x").has(Prio(1), Req("y").has(Prio(1)))).top == 
        Model(Prio(1), Req("x").has(Prio(1), Req("y")))

  test("Model append add        "):
    assert:
      Model(Prio(1),Prio(2),Undefined(Prio)) :+ Prio(42) == 
        Model(Prio(1), Prio(2),Undefined(Prio), Prio(42))

    assert:
      Model(Prio(1),Prio(2),Undefined(Prio)) + Prio(42) == 
        Model(Prio(42), Prio(42), Prio(42))

    assert:
      Model(Prio(1),Req("x"),Prio(2),Undefined(Prio),Req("x")).add(Prio(42)).distinctTopAttrType == 
        Model(Req("x"),Prio(42),Req("x"))

    assert:
      Model(Req("x").has(Req("y")), Req("y").has(Req("b")), Req("x").has(Req("z")), Req("y").has(Req("q")))
        .mergeFirst(Req("y").has(Req("a"))) ==
          Model(Req("x").has(Req("y")), Req("y").has(Req("b"), Req("a")), Req("x").has(Req("z")), Req("y").has(Req("q")))

    val m = """ 
          * Req 1 
            * Req 1.1
              * Req 1.1.1 has Prio 1
              * Req 1.1.2 has Prio 2
            * Req 1.2
              * Req 1.2.1
              * Req 1.2.2
          * 
          """.trim.toModel

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

  test("Model normal compact   "):
    val m = Model(
      Req("y").has(Prio(1),Prio(2),Req("z").has(Req("a"))),
      Req("y").has(Prio(1),Prio(2),Req("z").has(Req("b"))),
      Prio(42),
      Prio(41),
      Undefined(Prio),
      Undefined(Prio),
      Req("x"),
      Req("x").has(),
      Req("x"),
      Req("x").has(),
      Req("x").has(Prio(1),Prio(2),Req("z")),
      Req("x").has(Prio(1),Prio(2),Req("x").has(), Req("x")),
      Req("x").has(Prio(2),Prio(1),Req("y")),
      Req("y").has(Prio(1),Prio(2),Req("x")),
      Req("y").has(Prio(1),Prio(2),Req("y")),
      Req("y").has(Prio(1),Prio(2),Req("z")),
    )

    val compacted = Model(
      Req("y").has(
        Prio(2),
        Req("z").has(
          Req("a"),
          Req("b"),
        ),
        Req("x"),
        Req("y"),
      ),
      Undefined(Prio),
      Req("x").has(
        Prio(1),
        Req("z"),
        Req("x"),
        Req("y"),
      ),
    )
    
    val normalized = Model(
      Req("x").has(
        Prio(1),
        Req("x"),
        Req("y"),
        Req("z"),
      ),
      Req("y").has(
        Prio(2),
        Req("x"),
        Req("y"),
        Req("z").has(
          Req("a"),
          Req("b"),
        ),
      ),
      Undefined(Prio),
    )

    assert(m.compact == compacted)
    assert(m.normal == normalized)
  
    assert:
      Model(Prio(1),Prio(2),Req("x"),Req("y"),Req("x"),Prio(3)).distinctAttrTypeDeep ==
      Model(Req("x"),Req("y"),Req("x"),Prio(3))

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
      m.add(Req("zzz")).removeTop(Req("zzz")) == m 

    assert:
      m + Req("zzz") - Req("zzz") == m 

    assert:
      m.paths.toModel.compact == m.compact  //this example happens to be same order

    assert:
      m.paths.toModel.normal == m.normal  //this example happens to be same order

    assert:
      m.compact.sorted == m.normal  

    assert:
      m.paths.map(_.show).map(Path.fromString).map(_.get) == m.paths

    assert:
      m.split.join.normal == m.join.normal

    assert:
      m.split.join.split.normal == m.join.split.normal

    // run invariant tests on 100 random models
      
    val ms = Seq.tabulate(100)(i => Model.random(20))

    assert: 
      ms.forall(m => m.add(Req("zzz")).removeTop(Req("zzz")) == m)

    assert: 
      ms.forall(m => m.paths.map(_.show).map(Path.fromString).map(_.get) == m.paths)

    assert: 
      ms.forall(m => m.split.join.normal == m.normal)

    assert: 
      ms.forall(m => m.paths.toModel.normal == m.normal)

    assert: 
      ms.forall(m => m.split.join.normal == m.join.normal)

    assert: 
      ms.forall(m => m.split.join.split.normal == m.join.split.normal)

    assert: 
      ms.forall(m => m.split.atomic.sorted == m.atomic.split.sorted)

    assert: 
      ms.forall(m => m.toMarkdown.toModel.toMarkdown == m.toMarkdown)


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
    
    assert:
      m.sortLeafRelsBy(Order) == Vector(Req("d"), Req("a"), Req("b"), Req("c"), Req("e"))

    assert:
      m.sortLeafRelsBy(Order) == m.sortLeafRelsBy(Prio)

    assert: 
      m.sortLeafRelsBy(Prio).toModel.withRank(Order).toModel.sortLeafRelsBy(Order) == m.sortLeafRelsBy(Order)

  test("Model Examples          "):
    import examples.Prioritization.*
    assert(normalizedVotes(DollarTest).intValues.sum == 99)



