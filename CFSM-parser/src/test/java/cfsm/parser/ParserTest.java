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
package cfsm.parser;

import cfsm.domain.*;
import cfsm.syntax.SyntaxChecker;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import scala.Option;
import scala.Tuple2;

import static org.junit.Assert.assertTrue;

/**
 * Testing of {@link Parser}
 */
@RunWith(VertxUnitRunner.class)
public class ParserTest extends ParserHarness {


    @Test
    public void simpleParseTest() {
        String path = path("cfsm.json");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        CFSMConfiguration parse = Parser.parse(res._1.get());

        Machine machine = parse.machines.get("machine1");

        assertTrue(parse.protocol.equals("CFSM 0.2"));

        assertTrue(parse.machines.size() == 1);
        assertTrue(machine.name.equals("machine1"));
        assertTrue(machine.states.size() == 2);

        State state1 = machine.states.get("state1");
        State state2 = machine.states.get("state2");

        assertTrue(state1.type.equals(StateType.INITIAL));
        assertTrue(state2.type.equals(StateType.FINAL));

        assertTrue(machine.transitions.get("transition1").type.equals(TransitionType.PRIVATE));
        assertTrue(machine.transitions.get("transition1").condition.equals("None"));
        assertTrue(machine.transitions.get("transition1").from == state1);
        assertTrue(machine.transitions.get("transition1").to == state2);
    }

    @Test
    public void twoMachinesParseTest() {
        String path = path("cfsm1.json");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        CFSMConfiguration parse = Parser.parse(res._1.get());

        Machine machine1 = parse.machines.get("machine1");
        Machine machine2 = parse.machines.get("machine2");

        assertTrue(machine1 != null);
        assertTrue(machine2 != null);
    }

    @Test
    public void syntaxCheckToplevelCorrectTest() {
        String path = path("top_level_correct.json");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        assertTrue(res._2.equals(SyntaxChecker.OK()));
    }

    @Test
    public void syntaxCheckTopLevelAutomataIncorrectTest() {
        String path = path("top_level_incorrect_automata.json");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        String check = SyntaxChecker.check(res._1.get());
        assertTrue(!check.equals(SyntaxChecker.OK()));
    }

    @Test
    public void syntaxCheckTopLevelProtocolIncorrectTest() {
        String path = path("top_level_incorrect_protocol.json");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        String check = SyntaxChecker.check(res._1.get());
        assertTrue(!check.equals(SyntaxChecker.OK()));
    }

    @Test
    public void invalidJsonTest() {
        String path = path("invalid_json.txt");
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(path);
        assertTrue(!res._2.equals(SyntaxChecker.OK()));
    }

    @Test
    public void invalidMachine() {
        String validate = validateJSON("invalid_machine.json");
        assertTrue(!validate.equals(SyntaxChecker.OK()));
    }

    @Test
    public void invalidSameNameTransitions() {
        String validate = validateJSON("invalid_same_name_transitions.json");
        assertTrue(!validate.equals(SyntaxChecker.OK()));
    }

    @Test
    public void severtalinitial() {
        String validate = validateJSON("invalid_state_several_initial.json");
        assertTrue(!validate.equals(SyntaxChecker.OK()));
    }


    /**
     * Calling {@link Parser#validate(CFSMConfiguration)} function with parsed json by path
     */
    public String validateJSON(String path) {
        String absPath = path(path);
        Tuple2<Option<JsonObject>, String> res = SyntaxChecker.rawParse(absPath);
        CFSMConfiguration configuration = Parser.parse(res._1.get());
        return Parser.validate(configuration);
    }
}
