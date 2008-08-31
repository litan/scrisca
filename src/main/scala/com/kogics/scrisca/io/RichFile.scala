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
package com.kogics.scrisca.io

import java.io._
import java.nio.ByteBuffer

object RichFile {
  implicit def enrichFile(f: File) = new RichFile(f)
}

class RichFile(f: File) {
  def readAsString: String = {
//    println("Reading file: " + f)
    val flen = f.length.toInt; val buf = new Array[Byte](flen); val bbuf = ByteBuffer.allocate(flen)
    val fis = new FileInputStream(f); val fchannel = fis.getChannel
    try {
      while(bbuf.hasRemaining) fchannel.read(bbuf)
      bbuf.flip; bbuf.get(buf)
      new String(buf, "UTF8")
    }
    finally {
      fis.close // also closes channel
    }
  }
  
  def write(data: String)  {
    val fos = new BufferedOutputStream(new FileOutputStream(f))
    try {
      fos.write(data.getBytes)
//    println("File written: " + f)
    }
    finally {
      fos.close
    }
  }
}
