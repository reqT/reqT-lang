package reqt

export core.*
export show.toScala
export selection.*
export path.*

extension (elems: Vector[Elem]) 
  def toModel = Model(elems)
  def m       = Model(elems)

extension (s: String) 
  def p = println(s)
  def toModel = parser.parseModel(s)
  def m       = parser.parseModel(s)