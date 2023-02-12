# reqt-lang

* The reqt language helps to structure natural-language requirements into semi-formal models using common requirements engineering concepts. 

* The reqt language is a core part of the reqT tool for software requirements modelling.

* This repo includes a Scala library with a parser and other utilities for the reqt language. The reqt parser produces an immutable tree data structure called `Model` that is expressed using a Scala-embedded DSL, which also part of this library. 

* With the reqt Scala-embedded DSL you can analyze and transform your requirements models using the power of the Scala standard library and the extensive open source ecosystem of Scala, Java and Javascript. 

## How to build

`sbt build`