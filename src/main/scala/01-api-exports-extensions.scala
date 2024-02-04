package reqt

// the exports and extensions below is part of the surface api

export Model.toModel       // extension method on Vector[Elem]
export Show.show           // extension for pretty Model using enum types and apply 
export Selection.*         // and/or-expressions for selecting Model parts 
export ModelPath.*         // path factories for slash notation on Model
export ModelParser.m       // string interpolator to parse markdown Model
export ModelParser.toModel // string extension to parse markdown Model
export StringExtensions.*  // general extensions only depending on Scala stdlib


