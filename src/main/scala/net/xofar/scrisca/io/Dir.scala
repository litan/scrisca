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
package net.xofar.scrisca.io

import java.io.File
import scala.io.Source

// Work in progress. Not meant to be used yet 

object Dir {
  
  def walk(root: String, pattern: String)(fh: (File => Unit)) {
    val dir = new File(root);
    if (!dir.exists) throw new IllegalArgumentException("Non existent dir: " + root)
    walk(dir, pattern)(fh)
  } 
  
  def walk(file: File, pattern: String)(fh: (File => Unit)) {
    if (file.isDirectory) 
      file.listFiles.foreach(f => walk(f, pattern)(fh))
    else {
      if (file.getName.matches(pattern)) fh(file)
    }
  }
  
  def readFile(f: File): String = {
    println("Reading file: " + f)
    val sb = new StringBuilder()
    Source.fromFile(f).getLines.foreach {line => sb ++= line}
    sb.toString
  }
  
  
  def main(args: Array[String]) {
    walk("src", ".*\\.scala") { f => println(readFile(f))}
  }
}
