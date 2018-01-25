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
import cfsm.engine.Loggers.DefaultMaxEvents
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

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
  val SPACE = " "
  val DefaultMaxEvents = 10000

  /**
    * Log everything to specified file in CSV format
    *
    * @param file path to a file where the logs will be stored
    */
  def CSVLogger(file: FileLogger,
                delim: String,
                caseId: AtomicLong,
                eventId: AtomicLong,
                cmdOptions: CmdOptions,
                header: Boolean = false): Logger = {

    val usedCaseId = caseId.incrementAndGet()

    if (header)
      file.append(s"case-id${delim}event-id${delim}event-type${delim}timestamp$delim\n")

    {
      transitions =>

        val usedEventId = eventId.incrementAndGet()
        val dt = new DateTime(DateTimeZone.getDefault)
        val fmt = ISODateTimeFormat.dateTime
        val outRFC3339 = fmt.print(dt)

        transitions.foreach { transition =>

          val resultString = new StringBuilder()

          resultString.append(usedCaseId.toString)
          resultString.append(delim)
          resultString.append(usedEventId.toString)
          resultString.append(delim)
          resultString.append(transition.machine.name)
          resultString.append("-")
          resultString.append(transition.name())

          if (cmdOptions.showStates) {
            resultString.append("-")
            resultString.append(transition.from.name())
            resultString.append("->")
            resultString.append(transition.to.name())
          }

          if (cmdOptions.showConditions) {
            resultString.append("-")
            resultString.append(transition.condition)
          }

          resultString.append(delim)
          resultString.append(outRFC3339)
          resultString.append(EOL)

          file.append(resultString.toString())
        }
    }
  }

  /**
    * Log everything to specified file
    *
    * @param file path to a file where the logs will be stored
    */
  def SimpleFileLogger(file: FileLogger): Logger = {
    {
      case Nil =>
        file.close()
      case transitions =>
        transitions.foreach(transition => file.append(transition.toString + EOL))
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

case class CmdOptions(showStates: Boolean, showConditions: Boolean, maxEvents: Long = DefaultMaxEvents)