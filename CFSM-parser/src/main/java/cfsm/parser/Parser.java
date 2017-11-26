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
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Used for parsing given configuration file into {@link CFSMConfiguration}
 */
public class Parser {

    private final Vertx vertx;

    public Parser(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * @param path destination of configuration file
     * @return parsed configuration file
     */
    public CFSMConfiguration parse(String path) {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        final CFSMConfiguration[] config = new CFSMConfiguration[1];


        FileSystem protocol1 = vertx.fileSystem().readFile(path, hf -> {
            JsonObject entries = hf.result().toJsonObject();

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

                                    String type = state.getString("type").toUpperCase() ;
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

                                    return new Transition(transitionName, TransitionState.valueOf(type), condition, from, to);
                                }).collect(Collectors.toConcurrentMap(Transition::name, Function.identity()));

                        return new Machine(name, states, transitions);
                    }).collect(Collectors.toConcurrentMap(Machine::name, Function.identity()));


            config[0] = new CFSMConfiguration(protocol, machines);
            countDownLatch.countDown();
        });

        try {
            // wait for parsing
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return config[0];
    }
}