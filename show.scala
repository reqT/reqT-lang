package reqt

import lang.*

extension (a: Any) def show: String = a match
  case _: Int | Double | Float | Boolean => a.toString
  case _: String => s"\"$a\""