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
import java.io._
import java.util.Map
import java.util.concurrent.{CountDownLatch, TimeUnit}

object Process {

  implicit def dirName2dir(name: String): File = new File(name)
  implicit def enrichProcessBuilder(pb: ProcessBuilder)= new RichProcessBuilder(pb)
  implicit def byteArrayOs2String(bos: ByteArrayOutputStream): String = new String(bos.toByteArray(), "UTF8")
  
  /**
   * Execs the process specified by the command. Returns the combined stdout and stderr 
   * as a String; also returns the exit code 
   */
  def exec(cmd: String): (String, Int) = exec(cmd, ".")
  
  /**
   * Execs the process specified by the command. Pipes input from the supplied stream 
   * into the exec-ed process. Returns the combined stdout and stderr as a String; also 
   * returns the exit code 
   */
  def exec(cmd: String, in: InputStream): (String, Int) = {
    val pb = new ProcessBuilder(split(cmd))
    pb.directory(".")
    pb.redirectErrorStream(true)
    val ret = pb.exec(Some(in))
    (ret._1, ret._3)
  }
  
  /**
   * Execs the process specified by the command in the specified working directory. Returns 
   * the combined stdout and stderr as a String; also returns the exit code 
   */
  def exec(cmd: String, dir: String): (String, Int) = {
    val pb = new ProcessBuilder(split(cmd))
    pb.directory(dir)
    pb.redirectErrorStream(true)
    val ret = pb.exec(None)
    (ret._1, ret._3)
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
    val pb = new ProcessBuilder(split(cmd))
    pb.directory(dir)
    pb.redirectErrorStream(false)
    val ret = pb.exec(None)
    (ret._1, ret._2, ret._3)
  }
  
  def pipeExec(cmd: String): Nothing = {
    val pb = new ProcessBuilder(split(cmd))
    pb.directory(".")
    pb.redirectErrorStream(false)
    pb.exec(Some(System.in), System.out, Some(System.err))
    // our system.in is gone with the dead execed proc; nothing to do but bail
    System.exit(0)
    throw new RuntimeException("Should never get here")
  }
  
  private def split(cmd: String) = toJList(cmd.split("\\s+"))
  
  private def toJList[A](a: Array[A]): java.util.List[A] = {
    val l = new java.util.ArrayList[A]();
    a.foreach {l.add(_)}
    l
  }
  
  /**
   * Enriched Process Builder with exec methods that return process output and exit code
   */
  class RichProcessBuilder(pb: ProcessBuilder) {

    def exec(stdin: Option[InputStream]): (String, String, Int) = {
      val stdout = new ByteArrayOutputStream()
      val stderr = if(pb.redirectErrorStream) None else Some(new ByteArrayOutputStream())
      val retVal = exec(stdin, stdout, stderr)
      val err = stderr.getOrElse(new ByteArrayOutputStream())
      (stdout, err, retVal)
    }
    
    def exec(stdin: Option[InputStream], stdout: OutputStream, stderr: Option[OutputStream]): Int = {
      try {
        if (!pb.directory.exists) throw new IllegalArgumentException("Invalid Working Dir")
        
        var streams: List[(InputStream, OutputStream, String)] = Nil

        val proc = pb.start                                  
        
        streams = (proc.getInputStream, stdout, "<") :: streams
        stderr.foreach {is => streams = (proc.getErrorStream, is, "<<") :: streams }
        stdin.foreach {is => streams = (is, proc.getOutputStream, ">") :: streams }
        
        val latch = new CountDownLatch(streams.size)
        streams.foreach { str => 
          new Thread(new StreamLink(str._1, str._2, Some(latch), str._3)).start
        }
        
        val retCode = proc.waitFor
        latch.await(5, TimeUnit.SECONDS) // give streams some additional time to wind up
        
        retCode
      }
      catch {
      case e: Throwable => 
        val os = stderr.getOrElse(stdout)
        os.write(e.getMessage.getBytes("UTF8"))
        os.flush
        return -1;
      }
    }
  }
  
  protected [proc] class StreamLink(source: InputStream, dest: OutputStream, latch: Option[CountDownLatch], dxn: String) extends Runnable {
    private val BUF_SIZE = 1024
    def run = {
      val bSrc = new BufferedInputStream(source)
      val dst = new BufferedOutputStream(dest)
      var buffer = new Array[Byte](BUF_SIZE)
      var bread = 0
      try {
        while ({bread = bSrc.read(buffer); bread != -1}) {
          // println("StreamLink read: " + new String(buffer))
          // println(dxn)
          dst.write(buffer, 0, bread)
          dst.flush
        }
      }
      catch {
      case e: Throwable => dst.write(e.getMessage.getBytes("UTF8")) 
      }
      finally {
        // println("StreamLink done")
        dst.close
        latch.foreach {_.countDown}
      }
    }
  }
  
  def main(args: Array[String]): Unit = {
    
    if(args.size != 1) {
      println("Usage: Process cmd")
      System.exit(-1)
    }
    
    pipeExec(args(0))
    
    // pipeExec("scala.bat")
    // pipeExec("ghci")
    // pipeExec("irb.bat")            
    // pipeExec("C:\\cygwin\\bin\\echo hi there djfasfk sdafjasdkljfsda sadkfjaksldf sdafjaksdf")
    // println(exec("C:\\cygwin\\bin\\echo hi there")._1)
  }
}
