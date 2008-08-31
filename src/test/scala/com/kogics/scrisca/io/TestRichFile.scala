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

import org.junit._
import org.junit.Assert._
import java.io._
import TestData._
import RichFile._

class TestRichFile {
  @Test
  def testSmallReadWrite {
    readWriteTestHelper(smallDataset)
  }
  
  @Test
  def testMediumReadWrite {
    readWriteTestHelper(mediumDataset)
  }
  
  @Test
  def testLargeReadWrite {
    readWriteTestHelper(largeDataset)
  }
  
  def readWriteTestHelper(data: String) {
    val f = File.createTempFile("scrisca", "dir")
    f.write(data)
    val readData = f.readAsString
    assertEquals(data, readData)
  }

}
