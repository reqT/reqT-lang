package reqt

object selection:
  case class Link(e: Ent, rt: RelType):  //TODO should it be called RelHead?
    override def toString = s"$e & $rt"

  case class LinkType(et: EntType, rt: RelType):
    override def toString = s"$et & $rt"

  extension (et: Ent)      def &(rt: RelType)  = Link(et,rt)
  extension (et: EntType)  def &(rt: RelType)  = LinkType(et,rt)

  type Selection = SelectionTerm | SelectionExpr
  type SelectionTerm = Elem | ElemType | Link | LinkType

  case class SelectionExpr(terms: SelectionTerm*):
    override def toString = terms.mkString(" | ")

  extension (lhs: SelectionExpr) 
    def |(rhs: SelectionTerm): SelectionExpr = SelectionExpr((lhs.terms :+ rhs)*)
    def |(rhs: SelectionExpr): SelectionExpr = SelectionExpr((lhs.terms ++ rhs.terms)*)
    def &(rhs: RelType): SelectionExpr =
      val terms: Seq[SelectionTerm] = lhs.terms.map: term =>
        (term, rhs) match
          case (e: Ent, rt: RelType) => Link(e, rt)
          case (et: EntType, rt: RelType) => LinkType(et, rt)
          case pair => pair._1
      SelectionExpr(terms*)

  extension (lhs: SelectionTerm) 
    def |(rhs: SelectionTerm): SelectionExpr = SelectionExpr(lhs,rhs)
    def |(rhs: SelectionExpr): SelectionExpr = SelectionExpr((lhs +: rhs.terms)*)

  def apply(s: Selection, m: Model): Model = 
    val hasTerm = s match 
      case se: SelectionExpr => se.terms.toSet
      case st: SelectionTerm => Set(st)

    val pickedElems = m.elems.flatMap:
      elem => elem match
        case e: Ent     if hasTerm(e.et) || hasTerm(e) => Some(e) 

        case a: Attr[?] if hasTerm(a.at) || hasTerm(a) => Some(a)

        case r: Rel => 
          val sub = r.sub.keep(s)
          if sub.elems.nonEmpty then Some(Rel(r.e, r.rt, sub)) 
          else if 
            hasTerm(r.rt) ||  
            hasTerm(r.e & r.rt) || 
            hasTerm(r.e.et & r.rt) ||
            hasTerm(r) ||
            r.expandSubnodes.exists(rel => hasTerm(rel))
          then Some(Rel(r.e, r.rt, sub))
          else None

        case _ => None

    Model(pickedElems)

     
