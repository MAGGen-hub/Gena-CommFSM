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

import cfsm.domain._
import cfsm.engine.Loggers.SPACE

import scala.collection.JavaConverters._
import scala.collection.{immutable, mutable}

case class MiningMachine(machineModel: Machine) {

  // a machine storing messages here
  // sender <----> count_of_messages
  val mailBox: mutable.Map[MiningMachine, mutable.Map[String, Int]] =
  mutable.Map[MiningMachine, mutable.Map[String, Int]]()

  // a current state of machine
  @volatile
  var state: State = machineModel.initialState()

  /**
    * Force machine to change state
    *
    * @param transition  where to go
    * @param allMachines link to all machines
    * @return is operation successful
    */
  def goOnTransition(transition: Transition)
                    (implicit allMachines: Map[String, MiningMachine]): Unit = {
    transition.`type` match {
      case TransitionType.PRIVATE | TransitionType.SHARED => // nothing to do
      case TransitionType.RECM =>

        // we sure that the valued is present since it was ensured by MiningMachine#canGoOnTransition
        val machineAndMsg = recMachines(transition)
          .view
          .filter { machineAndMsg =>
            mailBox(allMachines(machineAndMsg._1)).getOrElseUpdate(machineAndMsg._2, 0) > 0
          }
          .take(1)
          .head

        val box = mailBox(machineAndMsg._1)
        val count = box(machineAndMsg._2)
        box.update(machineAndMsg._1, count - 1)


      case TransitionType.SENDM =>
        val machineAndMsg: immutable.Seq[(String, String)] = sendMachines(transition)
        machineAndMsg.foreach { mAndMsg =>
          val receiver = allMachines(mAndMsg._1)
          val mailMap = receiver.mailBox.getOrElseUpdate(this, mutable.Map(mAndMsg._2 -> 0))
          mailMap.put(mAndMsg._2, mailMap.getOrElse(mAndMsg._2, 0) + 1)
        }
    }
    state = transition.to
  }

  /**
    * Check for ability to go on given transition
    *
    * @param transition  transition to go
    * @param allMachines link to all machines
    * @return can we go?
    */
  def canGoOnTransition(transition: Transition)
                       (implicit allMachines: Map[String, MiningMachine]): Boolean = {
    transition.`type` match {

      // if we are not in appropriate state
      case _ if transition.from.name() != state.name() => false
      // if machine in final state then it is not possible to go
      case _ if state.`type` == StateType.FINAL => false

      // for these it just does not matter
      case TransitionType.PRIVATE | TransitionType.SHARED | TransitionType.SENDM => true
      case TransitionType.RECM => ableToRecm(transition)

    }
  }

  def ableToRecm(transition: Transition)
                (implicit allMachines: Map[String, MiningMachine]): Boolean = {

    // A ? msg1 :: B ? msg2  => List(("A","msg1"),("B,"msg2"))
    recMachines(transition)
      .view
      .filter { machineAndMsg =>
        mailBox
          .getOrElseUpdate(
            allMachines(machineAndMsg._1)
            , mutable.Map())
          .getOrElseUpdate(machineAndMsg._2, 0) > 0
      }
      .take(1)
      .headOption match {
      case None => false
      case Some(_) => true
    }
  }

  def recMachines(transition: Transition): List[(String, String)] = machineAndMsg(transition)("\\?")

  def sendMachines(transition: Transition): List[(String, String)] = machineAndMsg(transition)("\\!")

  def machineAndMsg(transition: Transition)(symb: String): List[(String, String)] = {
    transition.condition.split("::")
      .toList
      .map { statement =>
        val stmnts = statement.split(symb)
        (stmnts(0).trim, stmnts(1).trim)
      }
  }

  /**
    * @return available transitions to go
    */
  def enabledTransitions()(implicit allMachines: Map[String, MiningMachine]): mutable.Map[String, Transition] =
    state.outboundTransitions().asScala.filter(pair => this.canGoOnTransition(pair._2))
}