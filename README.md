[![Scala CI](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml/badge.svg)](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml)

# reqT-lang

* reqT-lang is a library and language for software requirements modelling written in Scala.

* reqT-lang helps you structure requirements into semi-formal natural-language models using common requirements engineering concepts. 

* reqT-lang is used by the command line and desktop tool [reqT](https://github.com/reqT).

* The reqT-lang library includes a parser and other utilities for the reqT language. The reqT parser produces an immutable tree-like data type called `Model` that is expressed using a Scala-embedded DSL. 

* With the reqT Scala-embedded DSL you can analyze and transform your requirements models using the power of the Scala standard library and the extensive open source ecosystem of Scala, Java and Javascript. 

## How to use reqT-lang as a library

You can manage your requirements with the reqT-lang library, the Scala compiler and your favorite editor, e.g. in VS Code with the Scala Metals extension.

### Use reqT-lang with scala-cli

* Install scala-cli from https://scala-cli.virtuslab.org/install

* Create a file `hello-reqt.scala` with this code:
```scala
//> using scala 3.4
//> using dep "reqt-lang:reqt-lang:4.0.0-RC2,url=https://github.com/reqT/reqT-lang/releases/download/4.0.0-RC2/reqt-lang_3-4.0.0-RC2.jar"

import reqt.*

extension (m: Model) 
  def trim =
    val empty = Text("")
    val elems: Vector[Elem] = 
      m.elems
        .reverse.dropWhile(_ == empty)
        .reverse.dropWhile(_ == empty)
    Model(elems)

@main def hello = 
  println("hello reqt")
  val m: Model = m"""
    * Feature hello has
      * Spec an informal greeting
  """.trim
  println(s"\nm.toString:\n$m")
  println(s"\nm.show:\n${m.show}")
  println(s"\nm.toMarkdown:\n${m.toMarkdown}")

```

* run with `scala-cli run hello-reqt.scala` and you should get this output:
```
hello reqt

m.toString:
Model(Rel(Ent(Feature,hello),Has,Model(StrAttr(Spec,an informal greeting))))

m.show:
Model(
  Feature("hello").has(
    Spec("an informal greeting"),
  ),
)

m.toMarkdown:
* Feature hello has Spec an informal greeting

```

### Use reqT-lang with sbt


## How to build the reqT-lang library

* You need the [`sbt`](https://www.scala-sbt.org/) build tool of [this version](https://github.com/reqT/reqT-lang/blob/main/project/build.properties) or higher on your path.

* Run `sbt build` in terminal.

## How to publish

For maintainers of https://github.com/reqT/reqT-lang

* Bump version in `build.sbt` and reload build

* Run `sbt package`

* Create a release on  https://github.com/reqT/reqT-lang

* Upload the jar in target/scala-x.y.z named something similar to reqT-lang_3-VERSION.jar