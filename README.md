[![Scala CI](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml/badge.svg)](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml)

# reqT-lang

* reqT-lang is a software requirements modelling language based on common requirements engineering concepts. 

* reqT-lang provides structure to natural language requirements, enabling analysis, graph generation and scripting.

* reqT-lang is used by the reqT desktop tool [reqT](https://github.com/reqT).

* The reqT-lang parser produces an immutable tree-like data structure called `Model` expressed in a Scala-embedded DSL, enabling analysis and transform of models using the powerful of Scala language and ecosystem. 

* reqT-lang is written in Scala and cross-compiled to the JVM, Javascript (TODO) and Native (Linux, TODO) runtimes.

* Documentation:
  * [reqT Quickref](TODO)
  * [reqT language specification](https://github.com/reqT/reqT-lang/blob/main/docs/langSpec-GENERATED.md) 
  * [reqT meta-concepts](https://github.com/reqT/reqT-lang/blob/main/docs/concepts-GENERATED.csv). 

## How to use reqT-lang as a library

You can manage your requirements with the reqT-lang library, the Scala compiler and your favorite editor, e.g. in VS Code with the Scala Metals extension.

### Use reqT-lang with scala

* Install scala from https://www.scala-lang.org/

* Create a file `hello-reqt.scala` with this code:
```scala
//> using scala 3.6.2
//> using dep "reqt-lang:reqt-lang:4.2.0,url=https://github.com/reqT/reqT-lang/releases/download/4.2.0/reqt-lang_3-4.2.0.jar"

import reqt.*

extension (m: Model) // build your own extensions on Model objects
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

* run with `scala run hello-reqt.scala` and you should get this output:
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

* The `build` task runs all these tasks but you can run each of them separately inside sbt:
  * `meta`    generate meta files, or use the underlying `Test / run` task
  * `test`    run all tests
  * `package` build a jar in target/scala-x.y.z
  * `build`   clean + all of the above


## How to publish

For maintainers of https://github.com/reqT/reqT-lang

* Bump version in `build.sbt` and reload build

* Run `sbt package`

* Create a release on  https://github.com/reqT/reqT-lang

* Upload the jar in target/scala-x.y.z named something similar to reqT-lang_3-VERSION.jar

The above is automated by `scala run publish.sh`