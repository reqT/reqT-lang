package reqt

import reqt.api.*

trait Show[-A]:
  def show(a: A): String

object Show:
  extension [A](a: A)(using s: Show[A]) 
    def show: String = s.show(a)
  
  given showInt: Show[Int] with
    override def show(a: Int): String = a.toString

  given showString: Show[String] with
    override def show(a: String): String = 
      if a.contains('"') || a.contains('\n') then s"\"\"\"$a\"\"\"" else s"\"$a\""

  given showElem: Show[Elem] with 
    override def show(e: Elem): String = e match 
      case Ent(et, id)     => s"${et.toString}(${id.show})"
      case u: Undefined[?] => u.toString
      case IntAttr(at, v)  => s"${at.toString}(${v.show})"
      case StrAttr(at, v)  => s"${at.toString}(${v.show})"
      case Rel(e, rt, sub) => s"""${e.show}.${rt.toString.toLowerCase}(${sub.elems.map(_.show).mkString(",")})"""
  
  given showModel: Show[Model] with 
    override def show(m: Model): String = 
      // TODO: make pretty indentation and nice line wrapping if long/deep model
      m.elems.map(_.show).mkString("Model(\n  ",",\n  ","\n)")

  given showAny: Show[ElemType] with
    override def show(et: ElemType): String = et.toString
