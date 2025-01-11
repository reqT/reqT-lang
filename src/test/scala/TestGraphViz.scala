package reqt

class TestGraphViz extends munit.FunSuite:
  test("Model toGraphVizNested"):
    import scala.sys.process._
    val m = examples.Lauesen.ContextDiagramInterfaces
    val dotNested = GraphvizGen.modelToGraphNested(m)
    val f1 = "target/g1n.dot"
    dotNested.saveTo(f1)
    if isDotInstalled() then dotCmd(f1).!

    val dotFlat = GraphvizGen.modelToGraphFlat(m)
    val f2 = "target/g1f.dot"
    dotFlat.saveTo(f2)
    if isDotInstalled() then dotCmd(f2).!


    assert(true)  // TODO generate a graph from dot and check if it is good 