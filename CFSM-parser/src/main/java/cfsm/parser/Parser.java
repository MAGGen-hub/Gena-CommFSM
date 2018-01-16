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
package cfsm.parser;

import cfsm.domain.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Used for parsing given configuration file into {@link CFSMConfiguration}
 */
public class Parser {


    /**
     * Checking for a two things:
     * 1. All SHARED transitions with the same name have the same conditions
     * 2. All machines should have exactly one initial state
     * 3. All RECM and SEND machines is exists
     * 4. Final states should not have outbound transitions
     *
     * @return error report or OK
     */
    public static String validate(CFSMConfiguration config) {
        StringBuilder result = new StringBuilder();

        // 1. All SHARED transitions with the same name have the same conditions
        long countOfBadSharedTransitions = config.machines
                .values()
                .stream()
                .flatMap(machine -> machine.transitions.values().stream())
                .filter(transition -> transition.type == TransitionType.SHARED)
                .collect(Collectors.groupingBy(Transition::name))
                .entrySet()
                .stream()
                .filter(entry -> {
                    String firstCondition = entry.getValue().get(0).condition;
                    for (Transition transition : entry.getValue()) {
                        if (!transition.condition.equals(firstCondition))
                            return true;
                    }
                    return false;
                }).count();
        if (countOfBadSharedTransitions != 0) {
            result.append("ERROR: All SHARED transitions with the same name should have the same conditions");
        }

        // 2. All machines should have exactly one initial state
        long countOfMachinesWithSeveralInitialStates = config.machines.values()
                .stream()
                .filter(machine -> {
                    long initialStatesCount = machine.states
                            .values()
                            .stream()
                            .filter(state -> state.type == StateType.INITIAL)
                            .count();
                    return initialStatesCount != 1;
                }).count();

        if (countOfMachinesWithSeveralInitialStates != 0) {
            result.append("ERROR: All machines should have exactly one initial state");
        }

        // 3. All RECM and SEND machines is exists
        long countOfBadRecmAndSendTransitions = config.machines
                .values()
                .stream()
                .flatMap(machine -> machine.transitions.values().stream())
                .filter(transition -> transition.type == TransitionType.RECM || transition.type == TransitionType.SENDM)
                .filter(transition -> {
                    String[] split = transition.condition.replaceAll("[!?]+", " ").trim().split(" ");
                    for (String machineName : split) {
                        if (config.machines.get(machineName) == null) {
                            return true;
                        }
                    }
                    return false;
                }).count();

        if (countOfBadRecmAndSendTransitions != 0) {
            result.append("ERROR: All RECM and SEND machines should be exists");
        }

        // 4. Final states should not have any outbound transitions
        long finalStateWithOutBoundTransitions = config.machines
                .values()
                .stream()
                .flatMap(machine -> machine.states.values().stream())
                .filter(state -> state.type == StateType.FINAL && state.outboundTransitions().size() != 0)
                .count();

        if (finalStateWithOutBoundTransitions != 0) {
            result.append("ERROR: Final states should not have any outbound transitions");
        }

        if (result.length() == 0) {
            return "OK";
        } else {
            return result.toString();
        }
    }

    /**
     * @param entries parsed to raw JsonObject config file
     * @return parsed configuration file
     */
    public static CFSMConfiguration parse(JsonObject entries) {
        String protocol = entries.getString("protocol");
        JsonArray automata = entries.getJsonArray("automata");

        Map<String, Machine> machines = automata.stream()
                .map(JsonObject.class::cast)
                .map((JsonObject a) -> {
                    // machine name
                    String name = a.getString("name");

                    // parse states
                    Map<String, State> states = a.getJsonArray("states").stream()
                            .map(JsonObject.class::cast)
                            .map(state -> {

                                String type = state.getString("type").toUpperCase();
                                String stateName = state.getString("name");

                                return new State(stateName, StateType.valueOf(type));
                            }).collect(Collectors.toConcurrentMap(State::name, Function.identity()));

                    // parse transitions
                    Map<String, Transition> transitions = a.getJsonArray("transitions").stream()
                            .map(JsonObject.class::cast)
                            .map(transition -> {

                                String transitionName = transition.getString("name");
                                String type = transition.getString("type").toUpperCase();
                                String condition = transition.getString("condition");
                                State from = states.get(transition.getString("from"));
                                State to = states.get(transition.getString("to"));

                                return new Transition(transitionName, TransitionType.valueOf(type), condition, from, to);
                            }).collect(Collectors.toConcurrentMap(Transition::name, Function.identity()));

                    return new Machine(name, states, transitions);
                }).collect(Collectors.toConcurrentMap(Machine::name, Function.identity()));
        return new CFSMConfiguration(protocol, machines);
    }
}