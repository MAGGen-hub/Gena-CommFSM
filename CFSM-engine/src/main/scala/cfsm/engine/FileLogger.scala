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

import java.io.{BufferedWriter, File}
import java.util.concurrent.ArrayBlockingQueue

import cfsm.engine.FileLogger.DefaultBufferSize

import scala.util.Try

object FileLogger {
  val DefaultBufferSize = 10

  def apply(file: File, bufferSize: Int = DefaultBufferSize): FileLogger = new FileLogger(file, bufferSize)
}

/**
  * Aimed to be used for log recording
  *
  * @param file       file to write logs
  * @param bufferSize queueSize were logs will be stored
  */
case class FileLogger(file: File, bufferSize: Int = DefaultBufferSize) extends Runnable {
  val appender: BufferedWriter = Loggers.appender(file)
  val queue: ArrayBlockingQueue[String] = new ArrayBlockingQueue[String](bufferSize)

  @volatile
  var isOpen = true

  /**
    * Will block thead until there are no space in queue
    *
    * @param text text to log
    */
  def log(text: String): Unit = {
    queue.put(text)
  }

  /**
    * Do the same as [[FileLogger.log()]]
    *
    * @param text text to log
    */
  def append(text: String): Unit = {
    log(text)
  }

  override def run(): Unit = {
    while (isOpen) {
      //IO exception may occur here
      Try {
        val queueElement = queue.take()
        appender.append(queueElement)
      }
    }
  }

  /**
    * Close logger
    */
  def close(): Unit = {
    while (!queue.isEmpty) {
      Thread.sleep(100)
    }
    isOpen = false
    queue.add("END")
    while (!queue.isEmpty) {
      Thread.sleep(100)
    }
    appender.flush()
    appender.close()
    println("done")
  }
}
