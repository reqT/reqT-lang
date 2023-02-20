package reqt

trait Show[-A]:
  def show(a: A): String

object Show:
  extension [A](a: A)(using s: Show[A]) 
    def show: String = s.show(a)

  given showIntString: Show[Int | String] with 
    override def show(a: Int | String): String = a match 
      case s: String => if s.contains('"') || s.contains('\n') then s"\"\"\"$s\"\"\"" else s"\"$s\""
      case i: Int  => i.toString

  given showElem: Show[Elem] with 
    override def show(e: Elem): String = e match 
      case e: Ent     => s"${e.et.toString}(${e.id.show})"
      case u: Undefined[?] => u.toString
      case a: Attr[? <: Int | String] => s"${a.at.toString}(${a.value.show})"
      case r: Rel     => 
        s"""${r.e.show}.${r.rt.toString.toLowerCase}(${r.sub.elems.map(_.show).mkString(",")})"""
  
  given showModel: Show[Model] with 
    override def show(m: Model): String = 
      // TODO: make pretty indentation and nice line wrapping if long/deep model
      m.elems.map(_.show).mkString("Model(\n  ",",\n  ","\n)")

  given showAny: Show[ElemType] with
    override def show(et: ElemType): String = et.toString
