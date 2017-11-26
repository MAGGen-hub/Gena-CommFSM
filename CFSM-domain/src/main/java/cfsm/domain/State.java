package cfsm.domain;

/**
 * The object is represents a {@link Machine} state
 */
public class State {

    // State name
    public final String name;
    // Can be: (INITIAL, FINAL, GENERAL)
    public final StateType type;

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
