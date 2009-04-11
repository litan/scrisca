/*
 * Copyright (C) 2008-2009 Lalit Pant <pant.lalit@gmail.com>
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

package com.kogics.scrisca.misc

import java.util.regex._
import java.io.File

object BlogHtmlCleaner {
    def main(args : Array[String]) : Unit = {
        if (args.size != 1) {
            Console.println("Please provide filename")
            System.exit(1)
        }
        Console.println(cleanHtml(inputString(args)))
    }

    def cleanHtml(html: String) : String = {
        val regex = """(<pre>)(.*?)(</pre>)"""
        regexReplace(html, regex, (x=>x), fixGenerics)
    }

    def fixGenerics(code : String) : String = {
        val regex = """(<)(.*?)(>)"""
        regexReplace(code, regex, bracesFixer, capitalize)
    }

    def bracesFixer(in: String) : String = in match {
        case "<" => "&lt;"
        case ">" => "&gt;"
        case _ => throw new RuntimeException("not an angle brace : " + in)
    }

    def regexReplace(in: String, regex: String, fouter : String => String, finner : String => String) : String = {
        val p = Pattern.compile(regex, Pattern.DOTALL)
        val m = p.matcher(in)
        val sb = new StringBuffer
        while (m.find())
        m.appendReplacement(sb, fouter(m.group(1)) + finner(m.group(2)) + fouter(m.group(3)))
        m.appendTail(sb);
        return sb.toString()
    }

    def capitalize(in : String) : String = {
        val chars : Array[char] = in.toCharArray
        chars(0) = Character.toUpperCase(chars(0))
        new String(chars)
    }

    def inputString(args : Array[String]) : String = {
        import io.RichFile._
        new File(args(0)).readAsString
    }
}