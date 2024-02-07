package reqt

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

  given showEntLink: Show[EntLink] with
    override def show(el: EntLink): String = s"${el.e.show}.${el.rt.show}"
      

  given showElem: Show[Elem] with 
    override def show(e: Elem): String = e match 
      case Ent(et, id)     => s"${et.toString}(${id.show})"
      case u: Undefined[?] => u.toString
      case IntAttr(at, v)  => s"${at.toString}(${v.show})"
      case StrAttr(at, v)  => s"${at.toString}(${v.show})"
      case Rel(e, rt, sub) => s"""${e.show}.${rt.show}(${sub.elems.map(_.show).mkString(",")})"""
  
  given showModel: Show[Model] with 
    override def show(m: Model): String = 
      val sb = scala.collection.mutable.StringBuilder("Model(\n")
      def indent(lvl: Int): String = "  " * lvl
      def loop(elems: Vector[Elem], indentLevel: Int): Unit = 
        if elems.isEmpty then () else
          elems.head match
            case Rel(e, rt, sub) => 
              sb.append(indent(indentLevel))
              if sub == Model.empty then 
                sb.append(EntLink(e, rt).show) 
                sb.append("(),\n")
                loop(elems.drop(1), indentLevel)
              else 
                sb.append(EntLink(e, rt).show)
                sb.append("(\n")
                loop(sub.elems, indentLevel + 1)
                sb.append(indent(indentLevel) + "),\n")
                loop(elems.drop(1), indentLevel)
            case e => 
              sb.append(indent(indentLevel))
              sb.append(e.show)
              sb.append(",\n")
              loop(elems.drop(1), indentLevel)
      end loop
      loop(m.elems, 1)
      sb.append(")")
      //m.elems.map(_.show).mkString("Model(\n  ",",\n  ","\n)")
      sb.toString

  given showElemType: Show[ElemType] with
    override def show(et: ElemType): String = et.toString

  given showRelType: Show[RelType] with
    override def show(rt: RelType): String = s"$rt".deCapitalize
