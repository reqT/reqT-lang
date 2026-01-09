package reqt

class TestParseUtils extends munit.FunSuite:
  import parseUtils.*

  test("isIdStart"):
    assert(isIdStart("hello"))
    assert(!isIdStart("123hello"))
    assert(!isIdStart("!hello"))
    assert(!isIdStart(""))

  test("parseInsideParen"):
    intercept[err.ParseException](parseInsideParen(""))
    assert(parseInsideParen("(hello)after") == ("hello", "after"))
    assert(parseInsideParen("(hel\"(stillinside)\"lo)after") == ("hel\"(stillinside)\"lo", "after"))
    assert(parseInsideParen("(hel\"\"\"(stillinside)\"\"\"lo)after") == ("hel\"\"\"(stillinside)\"\"\"lo", "after"))
    assert(parseInsideParen("(hel(strange)lo)after") == ("hel(strange)lo", "after"))
