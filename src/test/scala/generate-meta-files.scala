extension (s: String) infix def saveTo(f: String) = 
  val pw = java.io.PrintWriter(java.io.File(f), "UTF-8")
  try pw.write(s) finally pw.close()

val enumsFile = "src/main/scala/enums-GENERATED.scala"
val langSpecFile = "langSpec-GENERATED.md"

@main def generateMetaFiles = 
  println(s"Generating $enumsFile")
  reqt.meta.generate saveTo enumsFile
  println(s"Generating $langSpecFile")
  reqt.langSpec.generateMarkDown saveTo langSpecFile


