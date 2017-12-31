/*
*  MIT License
*  Copyright (c) 2017 PAIS-Lab-Public-Projects
*
*  Permission is hereby granted, free of charge, to any person obtaining a copy
*  of this software and associated documentation files (the "Software"), to deal
*  in the Software without restriction, including without limitation the rights
*  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*  copies of the Software, and to permit persons to whom the Software is
*  furnished to do so, subject to the following conditions:
*
*  The above copyright notice and this permission notice shall be included in all
*  copies or substantial portions of the Software.
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*  SOFTWARE.
*/

package cfsm.engine

import cfsm.domain.Transition

/**
  * Loggers aimed to record mining results
  */
//noinspection ScalaStyle
object Loggers {
  type Logger = Iterable[Transition] => Unit

  /**
    * Log everything to the console
    */
  val SimpleLogger: Logger = _.foreach(println)

  /**
    * Log everything to specified file in CSV format
    *
    * @param file path to a file where the logs will be stored
    */
  def CSVLogger(file: String): Logger = {
    ???
  }

  /**
    * Log everything to specified file
    *
    * @param file path to a file where the logs will be stored
    */
  def SimpleFileLogger(file: String): Logger = {
    ???
  }
}
