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
import cfsm.engine.Loggers.Logger
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import Matchers._
/**
  * Testing on [[cfsm.engine]] module
  */
@RunWith(classOf[JUnitRunner])
class EngineSpec extends TestBase {

  behavior of "engine"


  it should "work on basic example" in {
    // there are only one machine with one transitions
    val log: Logger = { trans => trans.toVector(0).name shouldBe "transition1"}
    withConfig("cfsm.json") { config: CFSMConfiguration =>
      mine(config, log, Selectors.RandomSelector)
    }
  }


  it should "work on example with " in {

    val int = new AtomicInteger(1)

    val log: Logger = { trans =>

      // transitions should be triggered one by one
      int.get() match {
        case 1 => trans.toVector(0).name shouldBe "transition1"
        case 2 => trans.toVector(0).name shouldBe "transition2"
        case _ => 1 shouldBe 2 //fail a test
      }
      int.incrementAndGet()
    }
    withConfig("cfsm1.json") { config: CFSMConfiguration =>
      mine(config, log, Selectors.RandomSelector)
    }
  }
}