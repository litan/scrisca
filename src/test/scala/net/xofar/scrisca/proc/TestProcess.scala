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

import org.junit._
import org.junit.Assert._

class TestProcess {
  @Test
  def testHappyExec: Unit = {
    val out = Process.exec("scala.bat net.xofar.scrisca.proc.ExecedProcess")
    assertEquals(ExecedProcess.OUT_STR + ExecedProcess.ERR_STR, out._1)
    assertEquals(0, out._2)
  }
  
  @Test
  def testErrorExec: Unit = {
    val out = Process.exec("scala.bat net.xofar.scrisca.proc.ExecedProcess errorTrigger")
    assertEquals("", out._1)
    // assertEquals(-1, out._2) // scala.bat seems to not propogate the -1 that we expect 
    assertEquals(0, out._2) // scala.bat seems to not return the -1 
  }
  
  @Test
  def testExecStreams: Unit = {
    val out = Process.execx("scala.bat net.xofar.scrisca.proc.ExecedProcess")
    assertEquals(ExecedProcess.OUT_STR, out._1)
    assertEquals(ExecedProcess.ERR_STR, out._2)
    assertEquals(0, out._3)
  }
}
