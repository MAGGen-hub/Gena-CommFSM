package cfsm.domain;

/**
 * The object is represents a transition inside a {@link Machine}
 */
public class Transition {

    // a name of transition
    public final String name;
    // can be one of: (PRIVATE, SHARED, SENDM, RECM)
    public final TransitionState type;
    // a condition for transition
    public final String condition;
    // a entering point for transitions
    public final State from;
    // destination of transition
    public final State to;

    /**
     * Construct a new transition
     *
     * @param name      a name of transition
     * @param type      can be one of: (PRIVATE, SHARED, SENDM, RECM)
     * @param condition a condition for transition
     * @param from      a entering point for transitions
     * @param to        destination of transition¬
     */
    public Transition(String name, TransitionState type, String condition, State from, State to) {
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.from = from;
        this.to = to;
    }
}
