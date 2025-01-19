package reqt

class TestHtmlGen extends munit.FunSuite:
  test("Model toHtml"):
    import scala.sys.process._
    val m = Model(
      Section("ContextDiagram").has(
        Product("x"),
        Image("ctxDiagram") has Location("ctx.png"),
      )
    )
    val html = m.toHtml
    val f1 = "target/m1.html"
    html.saveTo(f1)

    assert(true)  // TODO check that it includes the right stuff