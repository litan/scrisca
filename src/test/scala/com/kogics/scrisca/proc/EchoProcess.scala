package com.kogics.scrisca.proc

import java.io._

object EchoProcess {
  def main(args : Array[String]) : Unit = {
    val in = new BufferedReader(new InputStreamReader(System.in))
    val out: PrintStream = new PrintStream(System.out);
    var line: String = null
    
    while ({line = in.readLine(); line != null}) {
      if(System.getProperty("os.name").contains("Windows")) {
        out.print(line + "\r\n");  
      } else {
        out.print(line + "\n");
      }
    }
    System.exit(0)
  }
}
