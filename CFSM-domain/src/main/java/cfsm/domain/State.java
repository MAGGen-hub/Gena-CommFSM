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

import java.util.HashMap;
import java.util.Map;

/**
 * The object is represents a {@link Machine} state
 */
public class State {

    // State name
    private final String name;
    // Can be: (INITIAL, FINAL, GENERAL)
    public final StateType type;
    // where to go from the state?
    private Map<String, Transition> outboundTransitions = new HashMap<>();
    // where from someone can go here
    private Map<String, Transition> inboundTransitions = new HashMap<>();
    // related machine
    public Machine machine;

    public String name() {
        return name;
    }

    public Map<String, Transition> outboundTransitions() {
        return outboundTransitions;
    }

    public Map<String, Transition> inboundTransitions() {
        return inboundTransitions;
    }

    /**
     * Constructing new state
     *
     * @param name a name
     * @param type a type
     */
    public State(String name, StateType type) {
        this.name = name;
        this.type = type;
    }
}
