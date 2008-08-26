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
import java.io._
import java.util.Map
import java.util.concurrent.CountDownLatch

object Process {
  
  class StreamLink(source: InputStream, dest: OutputStream, latch: Option[CountDownLatch]) extends Runnable {
    def run = {
      val bSrc = new BufferedInputStream(source)
      val dst = new BufferedOutputStream(dest)
      var byte = 0;
      try {
        while ({byte = bSrc.read(); byte != -1}) {
          dst.write(byte)
        }
      }
      catch {
      case e: Throwable => dst.write(e.getMessage.getBytes("UTF8")) 
      }
      finally {
        dst.flush
        latch.foreach {_.countDown}
      }
    }
  }

  def exec(cmd: String): (String, Int) = exec(cmd, ".")
  def execx(cmd: String): (String, String, Int) = execx(cmd, ".")

  def exec(cmd: String, dir: String): (String, Int) = {
    try {
      val out = new ByteArrayOutputStream();
      val retCode = exec(cmd, dir, out, out);
      
      val output =  new String(out.toByteArray(), "UTF8");
      return (output, retCode);
    }
    catch {
    case e: Throwable => return (e.getMessage, -1)
    }
  }
  
  def execx(cmd: String, dir: String): (String, String, Int) = {
    try {
      val out = new ByteArrayOutputStream();
      val err = new ByteArrayOutputStream();
      val retCode = exec(cmd, dir, out, err);
      
      val output =  new String(out.toByteArray(), "UTF8");
      val error =  new String(err.toByteArray(), "UTF8");
      return (output, error, retCode);
    }
    catch {
    case e: Throwable => return ("", e.getMessage, -1)
    }
  }
  
  def exec(cmd: String, dir: String, stdout: OutputStream, stderr: OutputStream): Int = {
    
      val workingDir = new File(dir)
      if (!workingDir.exists) throw new IllegalArgumentException("Invalid Working Dir")

      // val proc = Runtime.getRuntime().exec(cmd, null, workingDir)
      val pb = new ProcessBuilder(cmd.split(' '))
      pb.directory(workingDir)
      if (stdout == stderr) pb.redirectErrorStream(true)
      val proc = pb.start                                  
      
      new StreamLink(proc.getInputStream, stdout, None).run
      
      if (!pb.redirectErrorStream) {
        val latch = new CountDownLatch(1)
        new Thread(new StreamLink(proc.getErrorStream, stderr, Some(latch))).start
        latch.await
      }

      proc.waitFor
  }
}
