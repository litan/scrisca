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

class TestDir {
  
  val (f1, f2, f3, f4) = ("file1", "file2", "file3", "file4")
  
  @Before
  def setup {
    new File("test-dir-tree/subdir1").mkdirs
    new File("test-dir-tree/subdir1/" + f1).createNewFile
    new File("test-dir-tree/subdir1/" + f2).createNewFile
    new File("test-dir-tree/subdir2").mkdirs
    new File("test-dir-tree/subdir2/" + f3).createNewFile
    new File("test-dir-tree/subdir2/" + f4).createNewFile
  }
  
  @Test
  def test1 {
    var files: List[String] = Nil
    Dir.walk("test-dir-tree", ".*") { f =>
      files = f.getName :: files
    }
    assertEquals(List("file1", "file2", "file3", "file4"), files.sort(_<_))
  }
  
  @After
  def teardown {
    new File("test-dir-tree/subdir1/" + f1).delete
    new File("test-dir-tree/subdir1/" + f2).delete
    new File("test-dir-tree/subdir2/" + f3).delete
    new File("test-dir-tree/subdir2/" + f4).delete
    new File("test-dir-tree/subdir1").delete
    new File("test-dir-tree/subdir2").delete
    new File("test-dir-tree").delete
  }
}
