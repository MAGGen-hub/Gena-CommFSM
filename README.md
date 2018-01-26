# Gene-CommFSM

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6ec021d65858412cb90dba82f2d4562a)](https://www.codacy.com/app/Sammers21/Gena-CommFSM?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=PAIS-Lab-Public-Projects/Gena-CommFSM&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/PAIS-Lab-Public-Projects/Gena-CommFSM.svg?branch=master)](https://travis-ci.org/PAIS-Lab-Public-Projects/Gena-CommFSM)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/6ec021d65858412cb90dba82f2d4562a)](https://www.codacy.com/app/Sammers21/Gena-CommFSM?utm_source=github.com&utm_medium=referral&utm_content=PAIS-Lab-Public-Projects/Gena-CommFSM&utm_campaign=Badge_Coverage)

Gene-CommFSM is a tool aimed to emulating behavior of 
communication of finite state machines.
The result of emulation can be saved to a file

### How to build

The project uses gradle as a build system. In order to build cli jar use:

```bash
# build jar
$ ./gradlew build
# run jar 
$ java -jar CFSM-dist/build/libs/cfsm-1.1.0.jar -help
# example usage with test config
$ java -jar CFSM-dist/build/libs/cfsm-1.1.0.jar -f ./CFSM-parser/src/test/resources/cfsm.json 
```

Note: make sure you have java 8/9 installed.

### Usage

```text
usage: cfsm
 -csv                       Format of logs is csv. Work only with '-d'
                            flag
 -d,--destination <arg>     Path where generated logs will be stored
 -elim,--max-events <arg>   maximum amount of events inside one generation
                            session
 -f,--file <arg>            Path to file with description of model
 -h,--help                  Print help message
 -sc,--show-conditions      Print conditions in csv log. Works only with
                            '-d' and '-csv' flag
 -ss,--show-states          Print states in csv log. Works only with '-d'
                            and '-csv' flag
```

### Example

Let's say you have a configuration file like this:
```json
{
  "protocol": "CFSM 0.2",
  "automata": [
    {
      "name": "A",
      "states": [
        {
          "type": "INITIAL",
          "name": "state1"
        },
        {
          "type": "FINAL",
          "name": "state2"
        }
      ],
      "transitions": [
        {
          "name": "transition1",
          "type": "SENDM",
          "condition": "!B",
          "from": "state1",
          "to": "state2"
        }
      ]
    },
    {
      "name": "B",
      "states": [
        {
          "type": "INITIAL",
          "name": "state1"
        },
        {
          "type": "GENERAL",
          "name": "state2"
        },
        {
          "type": "FINAL",
          "name": "state3"
        }
      ],
      "transitions": [
        {
          "name": "transition2",
          "type": "RECM",
          "condition": "?A",
          "from": "state1",
          "to": "state2"
        },
        {
          "name": "transition3",
          "type": "SENDM",
          "condition": "!C",
          "from": "state2",
          "to": "state3"
        }
      ]
    },
    {
      "name": "C",
      "states": [
        {
          "type": "INITIAL",
          "name": "state1"
        },
        {
          "type": "FINAL",
          "name": "state2"
        }
      ],
      "transitions": [
        {
          "name": "transition4",
          "type": "RECM",
          "condition": "?B",
          "from": "state1",
          "to": "state2"
        }
      ]
    }
  ]
}
```

The file can be at _CFSM-engine/src/test/resources/cfsm2.json_. And you want to generate a logs, the system produce.

That is how it can be done:
```bash$ java -jar CFSM-dist/build/libs/cfsm-1.1.0.jar -f CFSM-engine/src/test/resources/cfsm2.json
Specified path to file is: CFSM-engine/src/test/resources/cfsm2.json
Parsing configuration file......
Valid JSON: OK
Valid syntax: OK
Valid config objects: OK
3 machines found
A: state1 ---> state2
B: state1 ---> state2
B: state2 ---> state3
C: state1 ---> state2
```