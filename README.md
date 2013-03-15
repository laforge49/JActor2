PActor (POJO Actors)
======

The PActor project support Plain Old Java Objects as actors:
- Actors do not need to subclass an actor base class.
- Bytecode manipulation is not used. And
- There are no interfaces that an actor must implement.

There are only two requirements placed on an object to receive messages in a thread-safe way:
- Methods which receive a message must return a Request object. And
- All the Request objects created by the actor must be created with the same Mailbox object.

Message processing then is somewhat similar to using synchronization locks.
The advantage of using messaging is that the thread used to send a message does not block
unless the pend method was used. (Request implements send, reply and pend methods.)

PActor is a reimplementation of JActor, but without several features:
- There will be only one type of mailbox (asynchronous).
- All message passing will be via the mailbox.
- No message buffering, so there is no need for an outbox. The Mailbox will just have an inbox.
- No factories.
- No message routing to ancestors.
- Instead of reimplementing JAThreadManager, the slightly slower ThreadPoolExecutor will be used.

Features of JActor to be preserved:
- Both 1-way (events) and 2-way (request/response) messaging will be included.
- Exception handlers.

JActor features to be implemented in a later release:
- Loops.
- Simple machines.
- Continuations.

One new feature will be added in this project: the elimination of the need for a base class for actors. 
Any Plain Old Java Object (POJO) can function as an actor, given only a reference to a mailbox.
And there are no interfaces that need to be implemented either.

Status: No docs and only limited testing.

[Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
