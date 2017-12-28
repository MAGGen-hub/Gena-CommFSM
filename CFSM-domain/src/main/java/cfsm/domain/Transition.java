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

/**
 * The object is represents a transition inside a {@link Machine}
 */
public class Transition {

    // a name of transition
    public final String name;
    // can be one of: (PRIVATE, SHARED, SENDM, RECM)
    public final TransitionType type;
    // a condition for transition
    public final String condition;
    // a entering point for transitions
    public final State from;
    // destination of transition
    public final State to;
    // related machine
    public Machine machine;

    public String name() {
        return name;
    }

    /**
     * Construct a new transition
     *
     * @param name      a name of transition
     * @param type      can be one of: (PRIVATE, SHARED, SENDM, RECM)
     * @param condition a condition for transition
     * @param from      a entering point for transitions
     * @param to        destination of transition¬
     */
    public Transition(String name, TransitionType type, String condition, State from, State to) {
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.from = from;
        this.to = to;
    }
}
