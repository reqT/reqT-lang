[![Scala CI](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml/badge.svg)](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml)

# reqT-lang

* reqT-lang is a library and language for software requirements modelling.

* reqT-lang helps you structure requirements into semi-formal natural-language models using common requirements engineering concepts. 

* reqT-lang is used by the command line and desktop tool [reqT](https://github.com/reqT).

* The reqT-lang library includes a parser and other utilities for the reqT language. The reqT parser produces an immutable tree-like data type called `Model` that is expressed using a Scala-embedded DSL. 

* With the reqT Scala-embedded DSL you can analyze and transform your requirements models using the power of the Scala standard library and the extensive open source ecosystem of Scala, Java and Javascript. 

## How to use reqT-lang as a library

You can manage your requirements with the reqT-lang library, the Scala compiler and your favorite editor, e.g. in VS Code with the Scala Metals extension.

### Use reqT-lang with scala-cli

### Use reqT-lang with sbt


## How to build the reqT-lang library

* You need the [`sbt`](https://www.scala-sbt.org/) build tool of [this version](https://github.com/reqT/reqT-lang/blob/main/project/build.properties) or higher on your path.

* Run `sbt build`