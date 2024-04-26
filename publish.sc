// run with `scala-cli run publish.sc`

//> using scala 3.4
//> using toolkit default

println("*** Publish the reqT-lang jar to github using sbt and gh ***\n")

val wd = os.pwd

def yes(msg: String): Boolean = 
  scala.io.StdIn.readLine(msg).headOption.map(_.toUpper) == Some('Y')

def getFromBuild(key: String): Option[String] = util.Try{
    val lines = os.read(wd / "build.sbt").split("\n").toSeq
    val line: String = lines.filter(_.contains(s"val $key")).head
    val value: String = line.split("=").toSeq.last.trim.takeWhile(!_.isWhitespace)
    value.stripPrefix("\"").stripSuffix("\"")
  }.toOption

extension (s: Seq[String]) def showSeq = s.mkString(" ")

val scalaVer = getFromBuild("scalaVer").getOrElse("")
val reqTLangVer = getFromBuild("reqTLangVer").getOrElse("")

println("from build.sbt:")
println(s"""val reqTLangVer = "$reqTLangVer"""")
println(s"""val scalaVer    = "$scalaVer"""")

println(s"""\n*** Step 1: sbt "clean; package" """)

if yes("Do you want a clean build (Y/n)? ") then
  os.proc("sbt", "clean;package").call(cwd = wd, stdout = os.Inherit)


val dir = s"${os.pwd}/target/scala-$scalaVer"
val jar = s"$dir/reqt-lang_3-$reqTLangVer.jar"

if !os.exists(os.Path(jar)) then 
  println(s"Error: Missing jar-file; $jar")
  System.exit(1)
else
  println(s"\n*** Step 2: publish to github using gh")
  if reqTLangVer.isEmpty then 
      println("val reqTLangVer not found in build.sbt")
      sys.exit(1)
  else 
    val preRel = 
      if reqTLangVer.contains("-M") || reqTLangVer.contains("_RC") then Seq("--prerelease") else Seq()
    val assemblyCmd = Seq()
    val createCmd = Seq("gh", "release", "create", "v" + reqTLangVer, "--generate-notes") ++ preRel

    val uploadCmd = Seq("gh", "release", "upload", "v" + reqTLangVer, jar)

    val ghOpt = util.Try{os.proc("which", "gh").call(cwd = wd)}.toOption

    if ghOpt == None then 
      println("You need the github CLI 'gh' command on your path")
      println("Install from here: https://github.com/cli/cli/")

    println("\nRun these commands in terminal:\n")
    println(createCmd.showSeq)
    println(uploadCmd.showSeq)

