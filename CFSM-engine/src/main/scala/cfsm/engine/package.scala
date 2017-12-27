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

package cfsm

import cfsm.domain._

import scala.collection.JavaConversions._
import scala.collection.mutable

package object engine {

  /**
    * @param machines mining machines
    * @return transition_name <----> Set of transitions with such name
    */
  def getEnabledTransitions(machines: Map[String, MiningMachine]): Map[String, Iterable[Transition]] =
    machines.values
      .flatMap(machine => machine.state.outboundTransitions())
      .groupBy((pair: (String, Transition)) => pair._1)
      .map((pair: (String, Iterable[(String, Transition)])) => (pair._1, pair._2.map(_._2)))


  def move(setOfTransitionWeShouldMoveOn: Iterable[Transition]) = {

  }

  /**
    * Main engine function responsible for log mining
    *
    * @param config      machines configuration
    * @param logFunction when one iteration is done the function called in order to decide whether we should move next or not
    * @param selector    function responsible for figuring out which transition should be chosen
    */
  def mine(config: CFSMConfiguration, logFunction: Iterable[Transition] => Unit, selector: Iterable[String] => String): Unit = {

    val miningMachines = config.machines.map(pair => (pair._1, MiningMachine(pair._2))).toMap
    var enabledTransitions = getEnabledTransitions(miningMachines)

    // while there are a place to go
    while (enabledTransitions.nonEmpty) {

      // choosing a transition delegating responsibility to a selector function
      val selectedTransition = selector(enabledTransitions.keys)

      val setOfTransitionWeShouldMoveOn = enabledTransitions(selectedTransition)
      logFunction(setOfTransitionWeShouldMoveOn)

      move(setOfTransitionWeShouldMoveOn)

      // refresh
      enabledTransitions = getEnabledTransitions(miningMachines)
    }
  }


  case class MiningMachine(machineModel: Machine) {

    // a machine storing messages here
    // sender <----> count_of_messages
    val mailBox: mutable.Map[MiningMachine, Int] = mutable.Map[MiningMachine, Int]()

    // a current state of machine
    @volatile
    var state: State = machineModel.initialState()

    def goOnTransition(transition: Transition): Boolean = {

      val response = transition.from.name() == state.name()

      if (response) {
        transition.`type` match {
          case TransitionType.PRIVATE | TransitionType.SHARED => // nothing to do
          case TransitionType.RECM =>
          case TransitionType.SENDM =>
        }
        state = transition.to
      }

      response
    }
  }

  case class Message()

}
