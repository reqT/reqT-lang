package reqt

class TestGraphViz extends munit.FunSuite:
  test("Model toGraphVizNested"):
    val m = examples.Lauesen.ContextDiagramInterfaces
    val dot = GraphVizGen.modelToGraphNested(m)
    dot.saveTo("target/g1.dot")
    assert(true)  // TODO generate a graph from dot and check if it is good