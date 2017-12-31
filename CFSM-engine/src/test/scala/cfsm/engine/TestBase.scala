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

import java.util.concurrent.atomic.AtomicInteger

import cfsm.domain.CFSMConfiguration
import cfsm.parser.Parser
import cfsm.syntax.SyntaxChecker
import io.vertx.core.Vertx
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec}

/**
  * A base test template for engine module
  */
class TestBase extends FlatSpec with BeforeAndAfterEach with BeforeAndAfterAll {
  val vertx: Vertx = Vertx.vertx()
  var int = new AtomicInteger(1)


  override protected def beforeEach(): Unit = {
    int = new AtomicInteger(1)
  }

  override protected def afterAll(): Unit = {
    vertx.close()
  }

  /**
    * Execute something in context of config
    *
    * @param pathToConfig where a config stored
    * @param handler      a function to call with config object
    */
  def withConfig(pathToConfig: String)(handler: CFSMConfiguration => Unit): Unit = {
    val config = Parser.parse(SyntaxChecker.rawParse(pathToConfig)._1.get)
    handler(config)
  }
}

