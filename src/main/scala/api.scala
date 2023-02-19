package reqt

export model.*
export parser.{toModel, m}
export show.toScala
export selection.*
export path.*

extension (elems: Vector[Elem]) 
  def toModel = Model(elems)
  def m = Model(elems)

extension (s: String) def p = println(s)