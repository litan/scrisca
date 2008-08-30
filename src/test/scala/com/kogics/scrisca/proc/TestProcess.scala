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
package com.kogics.scrisca.proc

import org.junit._
import org.junit.Assert._
import java.io._
import java.util.concurrent.CountDownLatch

class TestProcess {
  
  val prefix = "java -cp \"." + File.pathSeparator + System.getProperty("java.class.path") + "\"" 
  val cmd =  prefix + " com.kogics.scrisca.proc.ExecedProcess"
  val echocmd =  prefix + " com.kogics.scrisca.proc.EchoProcess"
  
  @Test
  def testHappyExec {
    val out = Process.exec(cmd)
    assertEquals(ExecedProcess.OUT_STR + ExecedProcess.ERR_STR, out._1)
    assertEquals(0, out._2)
  }
  
  @Test
  def testErrorExec {
    val out = Process.exec(cmd + " errorTrigger")
    assertEquals("", out._1)
    // assertEquals(-1, out._2) // not reliable across OSs 
  }
  
  @Test
  def testExecStreams {
    val out = Process.execx(cmd)
    assertEquals(ExecedProcess.OUT_STR, out._1)
    assertEquals(ExecedProcess.ERR_STR, out._2)
    assertEquals(0, out._3)
  }
  
  @Test
  def testStreamLinkSmallData {
    streamLinkTestHelper(smallDataset)
  }
  
  @Test
  def testStreamLinkMediumData {
    streamLinkTestHelper(mediumDataset)
  }
  
  @Test
  def testStreamLinkLargeData {
    streamLinkTestHelper(largeDataset)
  }
  
  @Test
  def testEchoSmallData {
    echoDataTestHelper(smallDataset)
  }
  
  @Test
  def testEchoMediumData {
    echoDataTestHelper(mediumDataset)
  }
  
  @Test
  def testEchoLargeData {
    echoDataTestHelper(largeDataset)
  }
  
  def echoDataTestHelper(dataSet: String) {
    val is = new ByteArrayInputStream(dataSet.getBytes("UTF8"))
    val out = Process.exec(echocmd, is)
    assertEquals(dataSet, out._1.trim) // echo puts an extra newline at end
    assertEquals(dataSet.size, out._1.size-1) // echo puts an extra newline at end
  }
  
  def streamLinkTestHelper(dataSet: String) {
    val os = new ByteArrayOutputStream
    val is = new ByteArrayInputStream(dataSet.getBytes("UTF8"))
    val latch = new CountDownLatch(1)
    val sl = new Process.StreamLink(is, os, Some(latch), "*")
    new Thread(sl).start
    latch.await
    assertEquals(dataSet, new String(os.toByteArray, "UTF8"))
  }
  
  val smallDataset = "some random text;"
  val mediumDataset = """some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;"""
  
  val largeDataset = """some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;some random text;some random text;some random text;
                      some random text;"""
  
}
