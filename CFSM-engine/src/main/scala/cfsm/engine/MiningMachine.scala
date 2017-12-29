package cfsm.engine

import java.util

import cfsm.domain.{Machine, State, Transition, TransitionType}

import scala.collection.mutable

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
        mailBox.putIfAbsent(allMachines(machineName), 0)
        val currentMailCount: Int = mailBox(machineName)
        currentMailCount > 0
    }
  }

  /**
    * @return available transitions to go
    */
  def enabledTransitions()(implicit allMachines: Map[String, MiningMachine]): util.Map[String, Transition] =
    state.outboundTransitions().filter((pair: (String, Transition)) => this.canGoOnTransition(pair._2))
}