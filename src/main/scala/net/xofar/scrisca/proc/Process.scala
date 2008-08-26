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

  implicit def dirName2dir(name: String): File = new File(name)
  implicit def enrichProcessBuilder(pb: ProcessBuilder)= new RichProcessBuilder(pb)
  
  /**
   * Enriched Process Builder with exec methods that return process output and exit code
   */
  class RichProcessBuilder(pb: ProcessBuilder) {
    
    /**
     * Execs the process specified by the builder. Returns the combined stdout and stderr 
     * as a String; also returns the exit code 
     */
    def exec(): (String, Int) = {
      try {
        if (!pb.directory.exists) throw new IllegalArgumentException("Invalid Working Dir")

        pb.redirectErrorStream(true)
        val stdout = new ByteArrayOutputStream();

        val proc = pb.start                                  
      
        new StreamLink(proc.getInputStream, stdout, None).run

        val retCode = proc.waitFor
        val output =  new String(stdout.toByteArray(), "UTF8");
        return (output, retCode);
      }
      catch {
      case e: Throwable => return (e.getMessage, -1)
      }
    }
    
    /**
     * Execs the process specified by the builder. Returns stdout and stderr 
     * as Strings; also returns the exit code 
     */
    def execx(): (String, String, Int) = {
      try {
        if (!pb.directory.exists) throw new IllegalArgumentException("Invalid Working Dir")
        
        pb.redirectErrorStream(false)
        val stdout = new ByteArrayOutputStream();
        val stderr = new ByteArrayOutputStream();
        val latch = new CountDownLatch(1)
	
        val proc = pb.start                                  
	      
        new StreamLink(proc.getInputStream, stdout, None).run
	      
        new Thread(new StreamLink(proc.getErrorStream, stderr, Some(latch))).start
        latch.await
       
        val retCode = proc.waitFor
        val output =  new String(stdout.toByteArray(), "UTF8");
        val error =  new String(stderr.toByteArray(), "UTF8");
        return (output, error, retCode);
      }
      catch {
      case e: Throwable => return ("", e.getMessage, -1)
      }
    }
  }
  
  /**
   * Execs the process specified by the command. Returns the combined stdout and stderr 
   * as a String; also returns the exit code 
   */
  def exec(cmd: String): (String, Int) = exec(cmd, ".")
  
  /**
   * Execs the process specified by the command in the specified working directory. Returns 
   * the combined stdout and stderr as a String; also returns the exit code 
   */
  def exec(cmd: String, dir: String): (String, Int) = {
    val pb = new ProcessBuilder(cmd.split(' '))
    pb.directory(dir)
    pb.exec
  }
  
  /**
   * Execs the process specified by the command. Returns stdout and stderr 
   * as Strings; also returns the exit code 
   */
  def execx(cmd: String): (String, String, Int) = execx(cmd, ".")
  
  /**
   * Execs the process specified by the command in the specified working directory. Returns 
   * stdout and stderr as Strings; also returns the exit code 
   */
  def execx(cmd: String, dir: String): (String, String, Int) = {
    val pb = new ProcessBuilder(cmd.split(' '))
    pb.directory(dir)
    pb.execx
  }
  
  private class StreamLink(source: InputStream, dest: OutputStream, latch: Option[CountDownLatch]) extends Runnable {
    private val BUF_SIZE = 1024
    def run = {
      val bSrc = new BufferedInputStream(source)
      val dst = new BufferedOutputStream(dest)
      var buffer = new Array[Byte](BUF_SIZE)
      var bread = 0
      try {
        while ({bread = bSrc.read(buffer); bread != -1}) {
          dst.write(buffer, 0, bread)
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
}
