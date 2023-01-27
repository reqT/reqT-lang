package reqt

extension (a: Any) def show: String = 
  val s = a.toString
  a match
    case _: Int | Double | Boolean => s
    case _: String => if s.contains('"') || s.contains('\n') then s"\"\"\"$s\"\"\"" else s"\"$s\""
    case _: Float => s"${s}F"
    case _: Long => s"${s}L"
    case _ => s