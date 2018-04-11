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

import cfsm.domain.CFSMConfiguration
import cfsm.engine.Loggers.Logger
import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
  * Testing on [[cfsm.engine]] module
  */
//noinspection ScalaStyle
@RunWith(classOf[JUnitRunner])
class EngineSpec extends TestBase {

  behavior of "engine"

  it should "generate exactly as many events as specified" in {
    val limit = 20
    val log: Logger = {
      case Nil =>
      case so if int.get > limit => 1 shouldBe 2
      case trans => int.incrementAndGet()
    }

    withConfig("inf.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector, limit)
    }
  }

  it should "work on basic example" in {
    // there are only one machine with one transitions
    val log: Logger = {
      case Nil =>
      case trans => trans.toVector(0).name shouldBe "transition1"
    }
    withConfig("cfsm.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }

  it should "work on example with 2 machines communicated via message sending" in {
    val log: Logger = {
      case Nil =>
      case trans =>
        // transitions should be triggered one by one
        int.get() match {
          case 1 => trans.toVector(0).name shouldBe "transition1"
          case 2 => trans.toVector(0).name shouldBe "transition2"
          case _ => 1 shouldBe 2 //fail a test
        }
        int.incrementAndGet()
    }
    withConfig("cfsm1.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }

  // a bit more complicated of precious test
  it should "work on example with 3 machines communicated via message sending" in {
    val log: Logger = {
      case Nil =>
      case trans =>
        // transitions should be triggered one by one
        int.get() match {
          case 1 => trans.toVector(0).name shouldBe "transition1"
          case 2 => trans.toVector(0).name shouldBe "transition2"
          case 3 => trans.toVector(0).name shouldBe "transition3"
          case 4 => trans.toVector(0).name shouldBe "transition4"
          case _ => 1 shouldBe 2 //fail a test
        }
        int.incrementAndGet()
    }
    withConfig("cfsm2.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }

  it should "handle transitions with the same name" in {
    val log: Logger = {
      case Nil =>
      case trans =>
        // triggered 2 transitions at the same time and only once
        int.get() match {
          case 1 =>
            trans.size shouldBe 2
            trans.foreach(transition => transition.name shouldBe "transition1")
          case _ => 1 shouldBe 2 //fail a test
        }
        int.incrementAndGet()
    }
    withConfig("cfsm_same_name_transition.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }

  it should "handle transitions with the same name when condition is not fine" in {

    // the function should not be called in this case
    val log: Logger = {
      case Nil =>
      case trans =>
        1 shouldBe 2 //fail a test
    }

    withConfig("cfsm_same_name_transition_no_actions.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }

  it should "handle RECM transitions with several machines to receive messages from" in {

    val log: Logger = {
      case Nil =>
      case trans =>
        int.get() match {
          case 1 =>
            trans.foreach(transition => transition.name shouldBe "transition1")
          case 2 =>
            trans.foreach(transition => transition.name shouldBe "transition2")
          case _ => 1 shouldBe 2 //fail a test
        }
        int.incrementAndGet()
    }

    withConfig("several_rec_machines.json") { config: CFSMConfiguration =>
      emulate(config, log, Selectors.RandomSelector)
    }
  }
}