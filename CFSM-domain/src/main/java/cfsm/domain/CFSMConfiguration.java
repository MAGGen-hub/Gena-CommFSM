package cfsm.domain;

import java.util.List;

/**
 * Represents a parsed configuration specified by CFSM protocol specification
 */
public class CFSMConfiguration {

    // name and version of protocol. For example: "CFSM 0.2"
    public final String protocol;
    // list of machines in given configuration
    public final List<Machine> automata;

    /**
     * Constructs new configuration object
     *
     * @param protocol name and version of protocol
     * @param automata list of machines in given configuration
     */
    public CFSMConfiguration(String protocol, List<Machine> automata) {
        this.protocol = protocol;
        this.automata = automata;
    }
}
