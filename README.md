PActor (POJO Actors)
======

The PActor project support Plain Old Java Objects as actors:
- Actors do not need to subclass an actor base class.
- Bytecode manipulation is not used. And
- There are no interfaces that an actor must implement.

Message processing then is somewhat similar to using synchronization locks.
The advantage of using messaging is that the thread used to send a message does not block
unless the pend method was used. (Request implements send, reply and pend methods.)

The primary feature added in this project: the elimination of the need for a base class for actors. 
Any Plain Old Java Object (POJO) can function as an actor, given only a reference to a mailbox.

Status: Working but unstable.

[Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
