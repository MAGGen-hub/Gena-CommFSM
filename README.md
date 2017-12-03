# Gene-CommFSM

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6ec021d65858412cb90dba82f2d4562a)](https://www.codacy.com/app/Sammers21/Gena-CommFSM?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=PAIS-Lab-Public-Projects/Gena-CommFSM&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/PAIS-Lab-Public-Projects/Gena-CommFSM.svg?branch=master)](https://travis-ci.org/PAIS-Lab-Public-Projects/Gena-CommFSM)

Gene-CommFSM is a tool aimed to emulating behavior of 
communication of finite state machines.
The result of emulation can be saved to a file

### How to build

The project uses gradle as a build system. In order to build cli jar use:

```bash
# build jar
$ ./gradlew build
# run jar 
$ java -jar CFSM-dist/build/libs/cfsm-0.2.jar -help
# example usage with test config
$ java -jar CFSM-dist/build/libs/cfsm-0.2.jar -f ./CFSM-parser/src/test/resources/cfsm.json 
```

Note: make sure you have java 8 installed.

### Usage

```text
usage: cfsm
 -d,--destination <arg>   path where generated logs will be stored
 -f,--file <arg>          path to file with description of model
 -h,--help                print help message
```