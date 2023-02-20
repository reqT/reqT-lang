package reqt

export lang.*
export Show.show
export selection.*
export Path.*

export parser.m

extension (elems: Vector[Elem]) 
  def toModel = Model(elems)
  def m       = Model(elems)

extension (s: String) 
  def p = println(s)
  def toModel = parser.parseModel(s)
  def m       = parser.parseModel(s)