# NetEnt test by Lukas Voda

This repository contains the implementation of the
Home Assignment for Game Server Developer.

It implements the two games described in the assignment, and a simulation runner
to compute and verify the RTP of the games.

## Building the project

This is an SBT project. You need the SBT tool installed in your system
in order to build it.

```bash
sbt assembly
```

I have also included pre-built JAR file in the repository in
`./dist/netent-test-assembly-0.0.1-SNAPSHOT.jar`

It can be run with `java -jar dist/netent-test-assembly-0.0.1-SNAPSHOT.jar`. It runs
1 million simulation for both games and print out a result.

Example output:

```
13:13:45.396 [ioapp-compute-0] INFO  netent.Main - Result (id: 8c2a7289-e83b-4658-997b-700382d42593):
number of game rounds: 1000000
win total: 5990300
bet total: 8999360
RPT: 0.6656362230203037
13:13:46.641 [ioapp-compute-0] INFO  netent.Main - Result (id: 407f5051-ee63-4f15-be51-ddcc5247ed01):
number of game rounds: 1000000
win total: 1997410
bet total: 9999990
RPT: 0.19974119974119975
```

## Technologies and libraries

The code is written in Scala. It utilises the Cats Effect library as its
backbone for running (potentially asynchronous) computations.

The logging framework is Slf4j which is wrapped by a Cats Effect compatible
library log4cats.

The whole application is built on top of immutable data structures. That allows easier
reasoning about the code and doesn't require the programmer to track state while reading
the code.

The games are implemented as simple state machines. When invoked they generate
a new event internally and return the new state back to the caller.

## What would be next

The implementation is quite simple and covers only the main use cases.

If I had more time the next feature I would implement would be:

- wallet integration
    - reserving money in the player's wallet
    - deducting money after each spin
    - adding the prize money into the wallet
- better logging for the simulation test run
    - we could, for example, stream each round result into Cassandra or a similar database
      to obtain an immutable log of the simulation run
    - we are not logging each round of the game right now, it would
      be trivial to add but would slow down the simulation a lot
- more robust Random Number Generator
    - the game utilises the built-in pseudo-random number generator, but it
      structured in a way that allows a more robust solution to be plugged in easily
- acceptance tests for the simulation runner
