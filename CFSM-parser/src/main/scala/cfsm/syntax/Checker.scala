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

import io.vertx.core.json.JsonObject

import scala.collection.JavaConverters._

/**
  * Aimed to check for any syntax errors
  */
object Checker {

  val OK = "OK"

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
    "OK"
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
      TopLevelOnlyProtocolAndMachines
    )


  //  val SharedTransitionSameValueForSameNamesChecker
  val TopLevelOnlyProtocolAndMachines: JsonObject => String = {
    config =>
      val topLevelNames = config.fieldNames().asScala.toVector
      if (!topLevelNames.contains("protocol")) {
        "Top level elements should have \"protocol\" field"
      } else if (!topLevelNames.contains("automata")) {
        "Top level elements should have \"automata\" field"
      }
      else {
        OK
      }
  }
}