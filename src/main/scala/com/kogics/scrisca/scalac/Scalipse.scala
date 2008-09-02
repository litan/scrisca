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
package com.kogics.scrisca.scalac

import java.io.File
import xml._
import proc._
import eclipse._

/**
 * Runs scalac with eclipse classpath for a project - on a given file with given options  
 */
object Scalipse {
  def main(args : Array[String]) : Unit = {
    
    if (args.size < 3) {usage(); System.exit(1)} 
    
    val eclipseProjectLoc = new File(args(0)).getAbsolutePath
    val scalaFile = " " + args(1)
    
    val sb = new StringBuilder(" ")
    args.drop(2).foreach(sb ++ _ ++ " ")
    
    val scalaArgs = sb.toString
    
    var cmdName = "" 
    if(System.getProperty("os.name").contains("Windows")) 
      cmdName = "scalac.bat"
    else 
      cmdName = "scalac"
      
    val cmdLine = cmdName + scalaArgs + "-cp " + CpXtractor.extract(eclipseProjectLoc) + scalaFile
    
    // println("Scalac Wrapper Executing: " + cmdLine )
    
    val result = Process.exec(cmdLine, eclipseProjectLoc)
    println(result._1)
  }
  
  def usage() {
    println("Usage: Scalac eclipseProjectLocation scalaFile scalaArgs")
  }
}
