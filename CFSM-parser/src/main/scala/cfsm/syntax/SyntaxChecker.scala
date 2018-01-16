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

package cfsm.syntax

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
  * Aimed to check for any syntax errors
  */
object SyntaxChecker {

  val OK = "OK"
  val Automata = "automata"
  val NumberOfFieldsInTransition = 5

  /**
    * Checking for any errors in the input configuration
    *
    * @param input    input json with configuration
    * @param checkers set of defined checkers
    * @return OK or syntax error
    */
  def check(input: JsonObject, checkers: List[JsonObject => String]): String = {
    for (checker <- checkers) {
      val resOfCheck = checker(input)
      if (resOfCheck != OK) {
        return resOfCheck
      }
    }
    OK
  }

  /**
    * Checking for any errors in the input configuration
    *
    * @param input input json with configuration
    * @return OK or syntax error
    */
  def check(input: JsonObject): String = {
    check(input, DefaultListOfCheckers)
  }

  val DefaultListOfCheckers =
    List(
      TopLevelOnlyProtocolAndMachines(_),
      OnlyTransitionsAndStatesInsideMachine(_)
    )


  //  val SharedTransitionSameValueForSameNamesChecker
  val TopLevelOnlyProtocolAndMachines: JsonObject => String = {
    config =>

      config.fieldNames().asScala match {
        case fields if !fields.contains("protocol") => "Top level elements should have \"protocol\" field"
        case fields if !fields.contains(Automata) => s"Top level elements should have $Automata field"
        case _ => OK
      }
  }

  /**
    * Check for syntax errors in State objects
    */
  //noinspection ScalaStyle
  def checkState(state: JsonObject): List[String] = {
    var result = ListBuffer[String](OK)
    if (state.fieldNames().size() == 2) {
      if (state.fieldNames().contains("type")) {
        val type_field = state.getString("type")
        if (!Set("GENERAL", "INITIAL", "FINAL")(type_field)) {
          result += "field can be GENERAL, INITIAL or FINAL"
        }
      }
      else {
        result += "State should have a type field"
      }
      if (!state.fieldNames().contains("name")) {
        result += "State should have a name field"
      }
    }
    else {
      result += "State should have only two fields"
    }
    result.toList
  }

  /**
    * Check for syntax errors in Transition objects
    */
  //noinspection ScalaStyle
  def checkTransition(transition: JsonObject): List[String] = {
    var result = ListBuffer[String](OK)

    transition.fieldNames().size() match {
      case NumberOfFieldsInTransition =>
        var nextChecks = true
        transition.fieldNames().forEach { field =>
          if (!Set("from", "to", "name", "condition", "type")(field)) {
            nextChecks = false
            result += s"Transition should have a $field field"
          }
        }
        if (nextChecks) {
          val matchRes: String = transition.getString("type") match {
            case "SENDM" => if (!transition.getString("condition").contains("!")) "! should be used in SENDM transitions" else OK
            case "RECM" => if (!transition.getString("condition").contains("?")) "? should be used in SENDM transitions" else OK
            case "SHARED" => if (!isAllDigits(transition.getString("condition"))) "SHARED conditions require only digits in condition" else OK
            case "PRIVATE" => if (transition.getString("condition").equals("None")) "PRIVATE condition can be only None" else OK
            case trans_type => "Invalid transition type: " + trans_type
          }
          result += matchRes
        }
      case _ => result += "Transitions should have only five fields"
    }
    result.toList
  }

  def isAllDigits(x: String): Boolean = x forall Character.isDigit

  /**
    * Checking for any syntax errors
    *
    * @param machine machine to check
    * @return List of errors
    */
  def checkMachine(machine: JsonObject): List[String] = {
    var result = ListBuffer[String](OK)
    if (machine.fieldNames().size() == 3) {
      if (machine.fieldNames().contains("states")) {
        // check each state object
        machine.getJsonArray("states")
          .asScala
          .map(_.asInstanceOf[JsonObject])
          .foreach(state => result ++= checkState(state))
      } else {
        result + "Machine configuration should contain \"states\" field"
      }

      if (machine.fieldNames().contains("transitions")) {
        /// check each state object
        machine.getJsonArray("transitions")
          .asScala
          .map(_.asInstanceOf[JsonObject])
          .foreach(transition => result ++= checkTransition(transition))
      }
      else {
        result += "Machine configuration should contain \"transitions\" field"
      }
    }
    else {
      result += "Machine configuration should have only three fields"
    }
    result.toList
  }

  val OnlyTransitionsAndStatesInsideMachine: JsonObject => String = {
    config =>
      val automata: immutable.Seq[JsonObject] =
        config.getJsonArray(Automata)
          .asScala
          .toVector
          .map(_.asInstanceOf[JsonObject])

      automata
        .flatMap { machine: JsonObject => checkMachine(machine) }
        .reduce { (one, another) => if (one == OK) another else one }
  }


  /**
    * @param path path to json
    * @return (raw_json, syntax_warns)
    */
  def rawParse(path: String): (Option[JsonObject], String) = {
    val vertx = Vertx.vertx
    try {
      (Some(vertx.fileSystem.readFileBlocking(path).toJsonObject), OK)
    }
    catch {
      case _: Throwable => (None, "Given JSON is invalid")
    } finally {
      vertx.close()
    }
  }
}