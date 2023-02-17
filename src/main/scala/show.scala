package reqt

object show:
  extension [A](a: A)(using s: ShowAsScala[A]) 
    def toScala: String = s.showAsScala(a)

  trait ShowAsScala[-A]:
    def showAsScala(a: A): String

  object ShowAsScala:
    given showIntStringAsScala: ShowAsScala[Int | String] with 
      override def showAsScala(a: Int | String): String = a match 
        case s: String => if s.contains('"') || s.contains('\n') then s"\"\"\"$s\"\"\"" else s"\"$s\""
        case i: Int  => i.toString

    given showElemAsScala: ShowAsScala[Elem] with 
      override def showAsScala(e: Elem): String = e match 
        case e: Ent     => s"${e.et.toString}(${e.id.toScala})"
        case u: Undefined[?] => u.toString
        case a: Attr[? <: Int | String] => s"${a.at.toString}(${a.value.toScala})"
        case r: Rel     => 
          s"""${r.e.toScala}.${r.rt.toString.toLowerCase}(${r.sub.elems.map(_.toScala).mkString(",")})"""
    
    given showModelAsScala: ShowAsScala[Model] with 
      override def showAsScala(m: Model): String = 
        // TODO: make pretty indentation and nice line wrapping if long/deep model
        m.elems.map(_.toScala).mkString("Model(",",",")")

    given showAnyAsScala: ShowAsScala[ElemType] with
      override def showAsScala(et: ElemType): String = et.toString

  extension (m: Model) def toText: String = ??? 
