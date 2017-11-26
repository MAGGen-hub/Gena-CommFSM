package cfsm.domain;

import java.util.List;

/**
 * The object is represents a machine with ability to communicate
 */
public class Machine {

    // machine name
    public final String name;
    // List of related states
    public final List<State> states;
    //  List of transitions the machine have
    public final List<Transition> transitions;


    /**
     * Construct new machine with given properties
     *
     * @param name        a name
     * @param states      states the machine have
     * @param transitions transitions inside the machine
     */
    public Machine(String name, List<State> states, List<Transition> transitions) {
        this.name = name;
        this.states = states;
        this.transitions = transitions;
    }
}
