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
  def pick(sst: PickExpression | PickTerm, deep: Boolean = false): Model =
    val ts = sst match 
      case s : PickExpression => s.terms.toSet
      case t : PickTerm => Set(t)
    val pickedElems = m.elems.collect {
      case e: Ent     if ts.contains(e.et) || ts.contains(e) => e 

      case a: Attr[?] if ts.contains(a.at) || ts.contains(a) => a
      
      case r: Rel if deep => 
        val sub = r.sub.pick(sst, deep = true)
        if sub.elems.nonEmpty then Rel(r.e, r.rt, sub) else throw new NoSuchElementException
      
      case r: Rel if 
          ts.contains(r.rt) ||  
          ts.contains(r.e & r.rt) || ts.contains(r.e.et & r.rt) ||
          ts.contains(r) || r.expandSubnodes.exists(ts.contains)
        => 
          if !deep then r else Rel(r.e, r.rt, r.sub.pick(sst, deep = true)) 
    }
    Model(pickedElems)

  def pickDeep(sst: PickExpression | PickTerm): Model = pick(sst, deep = true)