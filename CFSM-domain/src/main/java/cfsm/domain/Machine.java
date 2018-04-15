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
package cfsm.domain;

import java.util.Map;
import java.util.Objects;

/**
 * The object is represents a machine with ability to communicate
 */
public class Machine {

    // machine name
    public final String name;
    // where we are starting to go
    private final State initialState;
    // List of related states
    public final Map<String, State> states;
    //  List of transitions the machine have
    public final Map<String, Transition> transitions;

    public String name() {
        return name;
    }

    /**
     * Construct new machine with given properties
     *
     * @param name        a name
     * @param states      states the machine have
     * @param transitions transitions inside the machine
     */
    public Machine(String name, Map<String, State> states, Map<String, Transition> transitions) {
        this.name = name;
        this.states = states;
        this.transitions = transitions;
        // TODO WARN If there are not only one Initial state
        initialState = states.values()
                .stream()
                .filter(state -> state.type == StateType.INITIAL)
                .findFirst()
                .orElse(null);
        buildTraverseGraph();
    }

    /**
     * Aimed to fill {@link State#outboundTransitions()}
     */
    private void buildTraverseGraph() {
        transitions.forEach((name, transition) -> {
            State from = transition.from;
            State to = transition.to;
            from.outboundTransitions().put(name, transition);
            to.inboundTransitions().put(name, transition);
            transition.machine = this;
            from.machine = this;
            to.machine = this;
        });
    }

    /**
     * @return is there are any initial state
     */
    public boolean hasInitialState() {
        return initialState == null;
    }

    /**
     * @return initial state of the machine
     */
    public State initialState() {
        return initialState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return Objects.equals(name, machine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
