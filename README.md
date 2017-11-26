# Gene-CommFSM

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
```

Note: make sure you have java 8 installed.

### Usage

usage: cfsm

 -d,--destination <arg>   path where generated logs will be stored
 
 -f,--file <arg>          path to file with description of model
 
 -h,--help                print help message
