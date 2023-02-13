extension (s: String) infix def saveTo(f: String) = 
  val pw = java.io.PrintWriter(java.io.File(f), "UTF-8")
  try pw.write(s) finally pw.close()

val modelFile = "src/main/scala/model-GENERATED.scala"
val langSpecFile = "langSpec-GENERATED.md"

@main def generateMetaFiles = 
  println(s"Generating $modelFile")
  reqt.meta.generate saveTo modelFile
  println(s"Generating $langSpecFile")
  reqt.langSpec.generateMarkDown saveTo langSpecFile


