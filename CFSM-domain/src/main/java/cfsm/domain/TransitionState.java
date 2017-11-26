package cfsm.domain;

/**
 * The object is represents a type of {@link Transition}
 */
public enum TransitionState {

    // Transition inside a machine. Does not require interaction with other machines.
    // The condition field for such transitions is always "None".
    PRIVATE,

    // Transition can be shared between machines.
    SHARED,

    // Transition which sends a message. Receivers can de found in condition field.
    SENDM,

    // Transition which receives a message. Senders whom messages we can consume can be found in
    // condition field.
    RECM
}
