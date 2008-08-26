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
package net.xofar.scrisca.proc

object ExecedProcess {
  
  val OUT_STR = "Out String; "
  val ERR_STR = "Error String"
  
  def main(args : Array[String]) : Unit = {
    if (args.size == 0) {
      System.out.print(OUT_STR)
      System.err.print(ERR_STR)
      System.exit(0)
    }
    else {
      System.exit(-1)
    }
  }
}
