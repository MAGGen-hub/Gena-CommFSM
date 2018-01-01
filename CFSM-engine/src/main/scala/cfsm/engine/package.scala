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
import cfsm.engine.Loggers.Logger
import cfsm.engine.Selectors.Selector

import scala.collection.JavaConverters._


package object engine {

  /**
    * @param machines mining machines
    * @return transition_name <----> Set of transitions with such name
    */
  def getEnabledTransitions(implicit machines: Map[String, MiningMachine]): Map[String, Iterable[Transition]] =
    machines.values
      .flatMap(_.enabledTransitions())
      .groupBy((pair: (String, Transition)) => pair._1)
      .map((pair: (String, Iterable[(String, Transition)])) => (pair._1, pair._2.map(_._2)))
      .filter {
        case (_, transitions) =>
          val vector = transitions.toVector
          vector(0).`type` match {
            // only if shared condition is fine
            case TransitionType.SHARED => vector.size >= vector(0).condition.toInt
            case _ => true
          }
      }

  /**
    * Main engine function responsible for log mining
    *
    * @param config machines configuration
    * @param log    when one iteration is done the function called in order to decide whether we should move next or not
    * @param select function responsible for figuring out which transition should be chosen
    */
  def mine(config: CFSMConfiguration, log: Logger, select: Selector): Unit = {

    implicit val miningMachines: Map[String, MiningMachine] = config.machines.asScala.map(pair => (pair._1, MiningMachine(pair._2))).toMap
    var enabledTransitions = getEnabledTransitions(miningMachines)

    // while there are a place to go
    while (enabledTransitions.nonEmpty) {

      // choosing a transition delegating responsibility to a selector function
      val selectedTransition: String = select(enabledTransitions.keys)

      val setOfTransitionWeShouldMoveOn: Iterable[Transition] = enabledTransitions(selectedTransition)
      log(setOfTransitionWeShouldMoveOn)

      // move on transitions
      setOfTransitionWeShouldMoveOn.foreach { transition =>
        val machineName = transition.machine.name
        val machine = miningMachines(machineName)
        machine.goOnTransition(transition)
      }

      // refresh enabled transitions
      enabledTransitions = getEnabledTransitions(miningMachines)
    }

    // report end of mining
    log(List())
  }
}