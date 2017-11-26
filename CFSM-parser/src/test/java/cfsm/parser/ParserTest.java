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
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Testing of {@link Parser}
 */
@RunWith(VertxUnitRunner.class)
public class ParserTest extends ParserHarness {


    @Test
    public void simpleParseTest() {
        String path = path("cfsm.json");
        CFSMConfiguration parse = parser.parse(path);

        Machine machine = parse.machines.get("machine1");

        assertTrue(parse.protocol.equals("CFSM 0.2"));

        assertTrue(parse.machines.size() == 1);
        assertTrue(machine.name.equals("machine1"));
        assertTrue(machine.states.size() == 2);

        State state1 = machine.states.get("state1");
        State state2 = machine.states.get("state2");

        assertTrue(state1.type.equals(StateType.INITIAL));
        assertTrue(state2.type.equals(StateType.FINAL));

        assertTrue(machine.transitions.get("transition1").type.equals(TransitionState.PRIVATE));
        assertTrue(machine.transitions.get("transition1").condition.equals("None"));
        assertTrue(machine.transitions.get("transition1").from == state1);
        assertTrue(machine.transitions.get("transition1").to == state2);
    }

}
