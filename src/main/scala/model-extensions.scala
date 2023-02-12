package reqt

case class Head(e: Ent, rt: RelType):
  override def toString = s"$e & $rt"

case class HeadType(et: EntType, rt: RelType):
  override def toString = s"$et & $rt"

extension (et: Ent)      def &(rt: RelType)  = Head(et,rt)
extension (et: EntType)  def &(rt: RelType)  = HeadType(et,rt)

type PickTerm = Elem | ElemType | Head | HeadType

case class PickExpression(terms: PickTerm*):
  override def toString = terms.mkString(" | ")

extension (s: PickExpression) 
  def |(st: PickTerm): PickExpression = PickExpression((s.terms :+ st)*)

extension (st: PickTerm) 
  def |(st2: PickTerm): PickExpression = PickExpression(st,st2)

extension (m: Model) 
  def keep(sst: PickExpression | PickTerm): Model =
    val ts = sst match 
      case s : PickExpression => s.terms.toSet
      case t : PickTerm => Set(t)

    val pickedElems = m.elems.flatMap ( elem => elem match
      case e: Ent     if ts.contains(e.et) || ts.contains(e) => Some(e) 

      case a: Attr[?] if ts.contains(a.at) || ts.contains(a) => Some(a)
      
      case r: Rel => 
        val sub = r.sub.keep(sst)
        if sub.elems.nonEmpty then Some(Rel(r.e, r.rt, sub)) 
        else if 
          ts.contains(r.rt) ||  
          ts.contains(r.e & r.rt) || ts.contains(r.e.et & r.rt) ||
          ts.contains(r) || r.expandSubnodes.exists(rel => ts.contains(rel))
        then Some(Rel(r.e, r.rt, sub))
        else None

      case _ => None
    )
    Model(pickedElems)
