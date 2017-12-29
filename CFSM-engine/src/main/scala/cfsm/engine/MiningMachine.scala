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

import java.util

import cfsm.domain.{Machine, State, Transition, TransitionType}

import scala.collection.mutable
import scala.collection.JavaConverters._

case class MiningMachine(machineModel: Machine) {

  // a machine storing messages here
  // sender <----> count_of_messages
  val mailBox: mutable.Map[MiningMachine, Int] = mutable.Map[MiningMachine, Int]()

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
                    (implicit allMachines: Map[String, MiningMachine]): Boolean = {
    val response = transition.from.name() == state.name()

    if (response) {
      transition.`type` match {
        case TransitionType.PRIVATE | TransitionType.SHARED => // nothing to do
        case TransitionType.RECM =>
          val machineName = transition.condition.substring(1)
          val currentMailCount: Int = mailBox(machineName)
          mailBox.put(machineName, currentMailCount - 1)
        case TransitionType.SENDM =>
          val machineName = transition.condition.substring(1)
          val receiver = allMachines(machineName)
          // send directly to the mailbox of target
          receiver.mailBox.get(this) match {
            case None => receiver.mailBox.put(this, 1)
            case Some(count) => receiver.mailBox.put(this, count + 1)
          }
      }
      state = transition.to
    }

    response
  }

  /**
    * Check for ability to go to given state
    *
    * @param transition  transition to go
    * @param allMachines link to all machines
    * @return can we go?
    */
  def canGoOnTransition(transition: Transition)
                       (implicit allMachines: Map[String, MiningMachine]): Boolean = {
    transition.`type` match {
      case TransitionType.PRIVATE | TransitionType.SHARED | TransitionType.SENDM =>
        true
      case TransitionType.RECM =>
        val machineName = transition.condition.substring(1)
        val resVal = mailBox.getOrElseUpdate(allMachines(machineName), 0)
        val currentMailCount: Int = mailBox(machineName)
        if (currentMailCount > 0) {
          mailBox.update(allMachines(machineName), resVal - 1)
          true
        }
        else {
          false
        }
    }
  }

  /**
    * @return available transitions to go
    */
  def enabledTransitions()(implicit allMachines: Map[String, MiningMachine]): mutable.Map[String, Transition] =
    state.outboundTransitions().asScala.filter(pair => this.canGoOnTransition(pair._2))
}