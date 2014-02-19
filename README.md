Robust and Composable Actors
=====

Actors are an important innovation as they make it easy to write thread-safe code free of race conditions.
This is because all changes to the state of an actor are made using the same thread.

But there are several problems with actors as they are generally conceived:

- Actors provide 1-way messaging with problematic support for request/response.
And there is no assurance that a response will be received.
- Excessive dependency on 1-way messaging sometimes leads to message flooding, which slows
garbage collection and gives rise to the occasional out-of-memory error.
- Actors tend to be fragile, with restarts by supervisors and consequently the potential for lost messages.

Actor Deadlocks
-----

Even when actors do not lock threads, deadlocks can occur because many actor frameworks support
selected message processing.
For example, actor A can send message X to actor B and then will not process any other type of message except
a message that is a response to message X.
Meanwhile actor B can send message Y to actor A and is not processing any other type of message except
a message that is a response to message Y.
At this point neither actor will process any messages.

Actor deadlocks do not occur in well designed actors. The problem is really that an actor can not be changed
safely without first considering the design of all the actors it interacts with. In practice, such reviews
are not always done as thoroughly as they should be and actor deadlocks begin occurring. Common practice is
to monitor actors and restart them as required.

JActor2 processes messages as they are received. It does not support the processing of selected messages.
But it does support message-specific state, so that updates to the actor state can be delayed until after
receiving a response to a message sent to another actor.

Results are Guaranteed
-----

JActor2 models operations/messages on Java method calls. Operations are implemented using 2-way messaging,
with the result being returned via a callback. And like a method call, an operation will either return a
normal result or an exception.

Operations are monitored to make sure they are behaving as expected and if not,
corrective action is taken and an exception is returned. So if there is a stack overflow error,
a runtime exception, or even if the operation takes too long to process, the situation is managed and
an exception is returned on the thread which sent the operation.

Architecture
-----

Actors in JActor2 are called reactors. Reactors are extended by adding blades.

A blades have state and a reference to the reactor they are a part of. A blade defines the operations on its state.
Blades can also directly call methods on other blades that are part of the same reactor.

Operations are called requests and are first class objects. An operation is defined as a class or as an
anonymous or nested class within a blade. A request is complete when there is a result and the request is
passed back to the originator of that request.

Links
-----

- [Core Tutorial](http://laforge49.github.io/JActor2/docs/tutorials/core/index.html) beta
- [API](http://laforge49.github.io/JActor2/docs/api/index.html)
- [Downloads](http://laforge49.github.io/JActor2/downloads)
- Dependencies: lsf4j, guava
- [Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
- License: [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
