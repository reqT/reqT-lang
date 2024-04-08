package reqt

// exports to shape the surface api


export Model.{toModel, concatAdjacent} // extension methods on Seq[Elem]

export Show.show   // extension for pretty Model using enum types and apply 
export Selection.* // and/or-expressions for selecting Model parts 
export Path.* // path factories for slash notation on Model

export MarkdownParser.m       // string interpolator to parse markdown Model
export MarkdownParser.toModel // string extension to parse markdown Model

export StringExtensions.* // general extensions only depending on Scala stdlib

export meta.{help, describe, findConceptGroup}  // help to explore concepts

export csp.* // types and extensions for expressing constraint satisfaction problems


