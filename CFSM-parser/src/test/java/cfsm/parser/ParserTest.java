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
    public void hello() {
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
