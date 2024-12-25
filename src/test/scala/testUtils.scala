package reqt

import scala.sys.process._

def isWindows = sys.props("os.name").startsWith("Windows")

def fixCmd(cmd: Seq[String]): Seq[String] = 
  if (isWindows) Seq("cmd","/C",s"""${cmd.mkString(" ")}""") else cmd

def runCmd(cmd: Seq[String]): Int = cmd.! 

def desktopOpen(f: String) = java.awt.Desktop.getDesktop().open( new java.io.File(f))

def isDotInstalled(): Boolean = runCmd(fixCmd(Seq("dot","-V"))) == 0

def fileSep = java.lang.System.getProperty("file.separator")

def fixSlash(s:String) = s.replace(fileSep, "/")

extension (s: String) 
  def stripAnySuffix: String = 
    if (s.contains('.')) s.split('.').dropRight(1).mkString(".") else s
  
  def suffix(suf: String):String = if (!s.endsWith(suf)) s + suf else s


def stripFileType(s: String) = {
  val ss = fixSlash(s).split('/')
  val head = ss.dropRight(1)
  val tail = ss.lastOption.map(_.stripAnySuffix).getOrElse("")
  (head ++ Seq(tail)).mkString("/") 
}
def newFileType(s: String, suf: String) = stripFileType(s).suffix(suf)

def dotCmd(fileName: String, format: String = "pdf", layout: String = "dot", moreArgs: Seq[String] = Seq()): Seq[String] = {
  val q = if (isWindows) '"'.toString else "" 
  val cmd = Seq("dot",s"-T$format",s"-K$layout") ++ moreArgs ++
    Seq("-o", s"""$q${newFileType(fileName, "." + format)}$q""",
      s"""$q${newFileType(fileName, ".dot")}$q""")
  fixCmd(cmd)
}  