/*
 * Copyright (C) 2008 Lalit Pant <lalit_pant@yahoo.com>
 *
 * The contents of this file are subject to the GNU General Public License 
 * Version 3 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.gnu.org/copyleft/gpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.kogics.scrisca.eclipse
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
