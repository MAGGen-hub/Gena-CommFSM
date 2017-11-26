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
package cfsm.dist;

import cfsm.domain.CFSMConfiguration;
import cfsm.parser.Parser;
import io.vertx.core.Vertx;
import org.apache.commons.cli.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        init(args);
    }

    private static void init(String[] args) {
        Vertx vertx = Vertx.vertx();
        try {

            // options specification
            Options options = new Options();

            options.addOption("f", "file", true, "path to file with description of model");
            options.addOption("h", "help", false, "print help message");
            options.addOption("d", "destination", true, "path where generated logs will be stored");

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            // if launched with -help
            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("cfsm", options);
                return;
            }

            String file = cmd.getOptionValue("file");
            String dist = cmd.getOptionValue("destination");

            // check for -file option
            if (file == null) {
                System.out.println("-file options is not specified");
                return;
            }

            System.out.println("Specified path to file is: " + file);

            // check file for existence
            if (!new File(file).exists()) {
                System.out.println(String.format("File \"%s\" is not exist", file));
                return;
            }

            if (dist == null) {
                System.out.println("Destination is not specified.");
            }

            System.out.println("Parsing configuration file......");

            Parser cfsmParser = new Parser(vertx);
            CFSMConfiguration configuration = cfsmParser.parse(new File(file).getAbsolutePath());
            System.out.println(String.format("%d machines found", configuration.machines.size()));

        } catch (ParseException e) {
            System.out.println("Not able to parse given options. Please, use help for details");
            e.printStackTrace();
        } finally {
            vertx.close();
        }
    }

}