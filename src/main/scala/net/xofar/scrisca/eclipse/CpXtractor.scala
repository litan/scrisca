package net.xofar.scrisca.eclipse
import java.io.File
import xml._

object CpXtractor {

  def extract(eclipseProjectLoc: String) = getClassPath(loadProjectXml(eclipseProjectLoc), eclipseProjectLoc)
  
  def loadProjectXml(projectLoc: String): Elem = XML.loadFile(projectLoc + "/.classpath")

  def getClassPath(rootElem: Elem, projectLoc: String): String = {
    val cpes = rootElem \ "classpathentry"
    val paths = cpes.filter(e => List("lib", "output").contains((e \ "@kind").toString)) \\ "@path"
    val sb = new StringBuilder
    paths.foreach(f => {
      val file = new File(f.toString)
      if (file.isAbsolute)
        sb.append(file.getAbsolutePath + ";")
      else
          sb.append(new File(projectLoc, f.toString).getAbsolutePath + ";")
    })
    sb.toString
  }
  
}
