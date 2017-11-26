package cfsm.domain;

/**
 * The object is represents a type of {@link State}
 */
public enum StateType {

    /*
     Initial of machine. Each machine should have ONLY one initial state.
     */
    INITIAL,

    /*
    Final state of machine. Once machine reached a state with the type - execution will be
    terminated immediately. A machine can have several states with the type.
    */
    FINAL,

    /*
    Intermediate state of the machine. The operation of the machine will continue
     */
    GENERAL
}
