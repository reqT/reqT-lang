[![Scala CI](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml/badge.svg)](https://github.com/reqT/reqT-lang/actions/workflows/scala.yml)

# reqT-lang

Docs at [reqt.github.io](https://reqt.github.io/)
* reqT-lang is a scalable modelling language based on [essential requirements engineering concepts](https://github.com/reqT/reqT-lang/releases/latest/download/reqT-quickref-GENERATED.pdf). 
* reqT-lang gives structure to natural language requirements, enabling analysis and visualization.
* reqT-lang is used by the reqT desktop tool [reqT](https://github.com/reqT).
* The reqT-lang parser produces an immutable tree-like data structure called `Model` expressed in a Scala-embedded DSL, enabling analysis and transform of models using the powerful of Scala language and ecosystem. 
* reqT-lang is written in Scala and cross-compiled to the JVM, Javascript (TODO) and Native (Linux, TODO) runtimes.

Visit:
* [reqT homepage](https://reqt.github.io/) with instructions on [how to get started](https://reqt.github.io/#getting-started-with-reqt)
* The [reqT Quickref](https://github.com/reqT/reqT-lang/releases/latest/download/reqT-quickref-GENERATED.pdf)
* All [reqT meta-concepts](https://github.com/reqT/reqT-lang/blob/main/docs/concepts-GENERATED.csv) in tabular format. 
* [reqT language specification](https://github.com/reqT/reqT-lang/blob/main/docs/langSpec-GENERATED.md) 

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

* Run `sbt build`

* Create a release on https://github.com/reqT/reqT-lang

* Upload the jar in target/scala-x.y.z named something similar to reqT-lang_3-VERSION.jar

* Upload target/metamodel-*

* Upload target/reqT-quickref-GENERATED.pdf

The above is automated by `scala run publish.sh`