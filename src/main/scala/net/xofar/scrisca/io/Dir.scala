package net.xofar.scrisca.io

import java.io.File

// Work in progress. Not meant to be used yet 

object Dir {
  
  def walk(root: String)(fh: (File => Unit)) {
    val dir = new File(root);
    if (!dir.exists) throw new IllegalArgumentException("Non existent dir: " + root)
    walk(dir)(fh)
  } 
  
  def walk(file: File)(fh: (File => Unit)) {
    if (file.isDirectory) 
      file.listFiles.foreach(f => walk(f)(fh))
    else
        fh(file)
  }
  
  def main(args: Array[String]) {
    walk(".") { f => println(f.getCanonicalPath)}
  }
}
