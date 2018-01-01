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


import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}
import java.util.concurrent.atomic.AtomicLong

import cfsm.domain.Transition
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.ISODateTimeFormat

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

  val EOL = "\n"

  /**
    * Log everything to specified file in CSV format
    *
    * @param file path to a file where the logs will be stored
    */
  def CSVLogger(file: File, delim: String, caseId: AtomicLong, eventId: AtomicLong): Logger = {
    val fileAppender = appender(file)

    val usedCaseId = caseId.incrementAndGet()
    fileAppender.append(s"case-id${delim}event-id${delim}event-type${delim}timestamp$delim\n")

    {
      case Nil =>
        fileAppender.close()
      case transitions =>

        val usedEventId = eventId.incrementAndGet()
        val dt = new DateTime(DateTimeZone.getDefault)
        val fmt = ISODateTimeFormat.dateTime
        val outRFC3339 = fmt.print(dt)

        transitions.foreach { transition =>
          fileAppender.append(usedCaseId.toString)
          fileAppender.append(delim)
          fileAppender.append(usedEventId.toString)
          fileAppender.append(delim)
          fileAppender.append(transition.machine.name)
          fileAppender.append("-")
          fileAppender.append(transition.from.name())
          fileAppender.append("->")
          fileAppender.append(transition.to.name())
          fileAppender.append("-")
          fileAppender.append(transition.condition)
          fileAppender.append(delim)
          fileAppender.append(outRFC3339)
          fileAppender.append(delim)
          fileAppender.append(EOL)
        }
    }
  }

  /**
    * Log everything to specified file
    *
    * @param file path to a file where the logs will be stored
    */
  def SimpleFileLogger(file: File): Logger = {
    val fileAppender = appender(file)

    {
      case Nil =>
        fileAppender.close()
      case transitions =>
        transitions.foreach(transition => fileAppender.append(transition.toString + EOL))
    }
  }


  /**
    * Return instance of [[BufferedWriter]]
    *
    * @param file path to file to open
    * @return object used for appending
    */
  def appender(file: File): BufferedWriter = {
    // create file
    if (!file.exists) {
      file.createNewFile
    }
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"))
  }
}
