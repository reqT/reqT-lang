package reqt

// the exports and extensions below is part of the surface api

export Show.show
export selection.*
export Path.*
export parser.{m, toModel, p}

extension (elems: Vector[Elem]) 
  def toModel = Model(elems)
  def m       = Model(elems)

