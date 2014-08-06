JActor2 is a multi-threaded OO programming model,
inspired by Alan Kay's early thoughts on [Objects](http://c2.com/cgi/wiki?AlanKaysDefinitionOfObjectOriented).
JActor2 is based on asynchronous 2-way messaging with assured responses.
The net result being code that is both simpler and more robust, and hence easier to maintain.

- [Background](#background)
    - [Multi-threading with Locks](#multi-threading-with-locks)
    - [Multi-threading with Actors](#multi-threading-with-actors)
- [Introducing JActor2](#introducing-jactor2)
    - [Synchronous Calls](#synchronous-calls)
    - [Asynchronous Sends](#asynchronous-sends)
    - [Exception Handling](#exception-handling)
    - [Operation Factories](#operation-factories)
    - [Parallel Processing](#parallel-processing)
- [Advanced Features](#advanced-features)
    - [Canceled Operations](#canceled-operations)
    - [Partial Failure](#partial-failure)
    - [Message Buffers](#message-buffers)
    - [Thread Migration](#thread-migration)
    - [OnIdle](#onidle)
- [Alternative Implementations](#alternative-implementations)
    - [Reactors](#reactors)
    - [Operation Invocations](#operation-invocations)
    - [Operation Types](#request-types)
- [Next Step](#next-step)
- [Links](#links)

Background
=====

Multi-threading with Locks
-----

Computers continue to increase in power, but they do so by adding more processing cores.
Over time then, applications which are not able to make use of all the threads supported by the newer
computers will end up using a smaller and smaller proportion of the available resources.

Problems often arise when more than one thread is executing the same code. This is not an issue for code that is
thread safe. But when code is not thread-safe, there can be non-deterministic
behavior, called race conditions, which vary depending on the exact order of execution and on if both threads share the
same memory cache.

Race conditions need to be identified and the unsafe code is typically surrounded by a lock which prevents more than
one code from executing the same code at the same time. But the identification of race conditions is often difficult,
and the use of locks can slow execution considerably.
When more than one lock is used, a deadlock can result if the locks are not always used by all thread in the same
order. The requirement of maintaining a consistent locking order sometimes becomes difficult as well, as the order must
be global in scope.

Multi-threading with Actors
-----

[Actors](http://en.wikipedia.org/wiki/Actor_model) are an alternative strategy to multi-threading with locks.
Actors are light-weight threads which interact via messages passed between them. Each actor has a queue of
pending messages (an inbox). And there is typically a thread pool used to process a queue of inactive actors
which have messages pending. Once an actor receives control, i.e. is assigned a thread, it process its pending
messages until there are none remaining and then releases the thread.

An actor never receives control from more than one thread at a time.
So the messages sent to an actor are processed one at a time.
This is how race conditions are prevented.
But there are several problems with actors:

- Actors provide 1-way messaging with problematic support for request/response.
And there is no assurance that a response will be received.
- Excessive dependency on 1-way messaging sometimes leads to message flooding, which slows
garbage collection and gives rise to the occasional out-of-memory error.
- Actors tend to be fragile, with restarts by supervisors and consequently the potential for lost messages.

Actors generally implement request/response (2-way messaging) in one of two ways,
either by blocking the thread until a response is
received, or by selecting only the expected response message for processing and processing other messages
only after the response has been processed. Either way, there is the
possibility of a deadlock occurring, depending on the design of the actor to which the request is sent.

The simplest case of deadlock is when two actors each send a request to the other. At this point, neither
actor will process any further messages. A review of each actor in isolation will not reveal any problems,
as this is a case of actor coupling.

Unfortunately, this problem rarely comes up in simple designs or initial implementations. It is only as
the code matures and the complexity increases that deadlocks begin to occur. Supervisors detect such failed
actors and restart them, which in turn gives rise to the increased chance that messages will be lost. So
timeouts are often added, further increasing the complexity of the code and potentially giving rise to an
increasing frequency of deadlocks.

Coupling, as we all know, is a bad thing. What we really need is another way to handle request/response, where
the processing of the response is specific to the request and with provision for intermediate state. This was the
starting point for JActor2.

Introducing JActor2
=====

JActor2 differs from other actor frameworks in several ways:

1. Operations are first-class objects, typically defined within the context of an actor's state
as a nested or anonymous class, so when
they are invoked, they can operate on that state. This is in contrast to other frameworks where
the operation is little more than a name and a set of parameters.
2. Uncaught exceptions and responses are passed back to the context from which an operation was invoked,
modeling the way exceptions and return values are handled with Java method calls.
3. For every operation that is invoked on another actor, there is every assurance that a response or exception
will be passed back.
4. Messages (operation invocations/responses) are processed in the order they are received by an actor.

There are three things of particular note here:

1. Uncaught exceptions are passed back to the context which invoked an operation, i.e. to the context most likely able
to handle those exceptions. This differs from more traditional actor frameworks where the supervisor of an actor
must handle the exceptions without knowledge of the context from which they arose.
2. A response or exception is passed back for every operation that is invoked, though processing is entirely asynchronous.
This is largely the result of modeling operations after Java method calls, but with additional support for detecting
infinite loops and erroneous processing.
3. Messages are not selected for processing based on the state of the actor, but are processed in the order received.
Nor does an actor block its thread when waiting for a response.
Actors then are not coupled, so
deadlocks are less likely and maintenance is much easier over the life of a project.

Before going any further, we need to define a few terms:

- Actors in JActor2 are called
[reactors](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/reactors/package-summary.html).
Reactors are composable.
- The components of a reactor are called
[blades](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/blades/package-summary.html).
A blade has state and a reference to the reactor it is a part of,
though the default constructor of a blade will often create its own reactor.
Blades define the operations which access their state.
- Messages are used to pass operations and responses between treads.
Operations are bound to a blade or reactor and are processed (executed)
only on the reactor's thread.
After an operation has been processed, the response
is passed back to the reactor which invoked the operation.
- When an operation is sent by one actor to another actor, a callback is assigned to the message.
The callback is a subclass of
[AsyncResponseProcessor](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/requests/AsyncResponseProcessor.html)
and has a single method, processAsyncResponse.
When a response is passed back to the originating reactor, the processAsyncResponse method is called on the reactor's thread.
- The [Plant](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/plant/package-summary.html)
is a singleton which creates the thread pool used by the reactors.
Plant's methods are all static.

Synchronous Calls
-----

Reactors mostly interact with other reactors, but it is not turtles all the way down. Java programs begin of course
with a main method. To pass an operation to a reactor, we use the operation's call method.

The call method is synchronous. The thread is blocked until a response or an exception is passed back.
The return value of the call method is the response assigned by the operation when it is evaluated by the
reactor. But when an exception is passed back, it is thrown.

```java

    import org.agilewiki.jactor2.core.impl.Plant;

    public class Simple {
        public static void main(final String[] _args) throws Exception {
            new Plant();
            try {
                A a = new A();
                a.startAOp().call();
            } finally {
                Plant.close();
            }
        }
    }
```

1. A Plant is created. Plant in turn creates the thread pool.
2. A blade, A, is created, which in turn creates its own reactor.
3. An operation bound to blade A, Start, is created.
4. The Start operation is added to the inbox of A's reactor.
5. The main thread waits for an assured response or an exception. (A
[ReactorClosedException](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/reactors/ReactorClosedException.html)
is thrown if the Start operation hangs.)
6. The plant is closed, which in turn closes blade A's reactor and the thread pool.

![Image](images/call.jpg)

Asynchronous Sends
-----

Messages are always passed asynchronously between reactors. Two-way messages are passed
using the send method on an AsyncRequestImpl object. And the two arguments to send are
(1) the operation to be invoked on the target reactor and (2) the callback to be executed
on completion of that operation.

A request/response exchange between actors does not block.

Let use say that a Start operation in blade A is to send an Add1 operation to blade B.

```java

    import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
    import org.agilewiki.jactor2.core.requests.AOp;
    import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
    import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

    class A extends NonBlockingBladeBase {
        final B b;

        public A() throws Exception {
            b = new B();
        }

        AReq<Void> startAOp() {
            return new AOp<Void>("start", getReactor()) {
                @Override
                protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                   final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                        @Override
                        public void processAsyncResponse(Void _response) throws Exception {
                            System.out.println("added 1");
                            _asyncResponseProcessor.processAsyncResponse(null);
                        }
                    };
                    _asyncRequestImpl.send(b.add1AOp(), startResponse);
                }
            };
        }
    }

    class B extends NonBlockingBladeBase {
        private int count;

        public B() throws Exception {
        }

        AReq<Void> add1AOp() {
            return new AOp<Void>("add1", getReactor()) {
                @Override
                protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                   AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    count += 1;
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            };
        }
    }
```

1. Blade B is created in the constructor of A.
2. The startResponse is created.
2. The startResponse is sent with the Start operation to the inbox of blade A's reactor.
3. Blade A's reactor evaluates the Start operation. The Start operation creates the Add1 operation.
4. The  Add1 operation is send to the inbox of Blade B's reactor.
5. Blade B's reactor evaluates the Add1 operation. The Add1 operation adds 1 to blade B's count.
6. The Add1 operation is assigned a result of null and is passed back to blade A's reactor.
7. The startResponse callback is evaluated by blade A's reactor. The callback prints "added 1".
8. The Start operation assigns a result of null which is passed back to the reactor that invoked the
Start operation.

![Image](images/send.jpg)

Exception Handling
-----

When an exception is raised and uncaught while processing an operation, the natural thing to do is to pass that exception
back to the context that invoked the operation. It would be nice to use try/catch to intercept that exception in the originating
operation, but that is simply not possible. So we use an
[ExceptionHandler](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/requests/ExceptionHandler.html)
instead.

```java

    import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
    import org.agilewiki.jactor2.core.requests.AOp;
    import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
    import org.agilewiki.jactor2.core.requests.ExceptionHandler;
    import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

    class A extends NonBlockingBladeBase {
        public A() throws Exception {
        }

        AOp<Void> startAOp() {
            return new AOp<Void>("start", getReactor()) {
                @Override
                protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                  final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    B b = new B();

                    AsyncResponseProcessor<Void> woopsResponse = new AsyncResponseProcessor<Void>() {
                        @Override
                        public void processAsyncResponse(Void _response) throws Exception {
                            System.out.println("can not get here!");
                            _asyncResponseProcessor.processAsyncResponse(null);
                        }
                    };

                    ExceptionHandler<Void> exceptionHandler = new ExceptionHandler<Void>() {
                        @Override
                        public void processException(final Exception _e,
                                                     final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                                throws Exception {
                            if (_e instanceof IOException) {
                                System.out.println("got IOException");
                                _asyncResponseProcessor.processAsyncResponse(null);
                            } else
                                throw _e;
                        }
                    };

                    _asyncRequestImpl.setExceptionHandler(exceptionHandler);
                    _asyncRequestImpl.send(b.woopsAOp(), woopsResponse);
                }
            };
        }
    }

    class B extends NonBlockingBladeBase {
        public B() throws Exception {
        }

        AOp<Void> woopsAOp() {
            return new AOp<Void>("woops", getReactor()) {
                @Override
                protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                  AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    throw new IOException();
                }
            };
        }
    }
```

1. Blade B is created in the constructor of the Start operation.
2. The woopsResponse is created.
3. The exceptionHandler is created.
4. The Start operation is sent to the inbox of blade A's reactor.
5. Blade A's reactor evaluates the Start operation.
The start operation is assigned the exceptionHandler.
6. A Woops operation is created.
7. The woopsResponse is sent with the Woops operation to the inbox of blade B's reactor.
8. Blade B's reactor evaluates the Woops operation,
which throws an IOException.
9. The Woops operation assigns a result of IOException and is passed back to to blade A's reactor.
10. The exceptionHandler is evaluated by blade A's reactor with a value of IOException, and
prints "got IOException"
11. The Start operation assigns a result value of null which
is passed back to the reactor which originated the Start operation.

![Image](images/exceptionHandler.jpg)

When an operation does not have an exception handler, any uncaught or unhandled exceptions are simply passed up
to the originating operation. Exceptions then are handled very much as they are when doing a method call.

There is a huge advantage to this approach. When an operation is sent, the originating operation will **always** get
back either a result or an exception. So you do not need to write a lot of defensive code, making your applications
easier to write and naturally more robust.

Operation Factories
-----

Nested classes impede decoupling, which is important for clarity, testing and reusability.
But by introducing operation factory methods we can then use interfaces to decouple our blades.

```java

    import org.agilewiki.jactor2.core.impl.Plant;
    import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
    import org.agilewiki.jactor2.core.requests.AOp;
    import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
    import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

    interface B {
        AOp<Void> add1AOp();
    }

    class BImpl extends NonBlockingBladeBase implements B {
        int count;

        public BImpl() throws Exception {
        }

        @Override
        public AOp<Void> add1AOp() {
            return new AOp<Void>("add1", getReactor()) {
                @Override
                protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                  final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    count = count + 1;
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            };
        }
    }

    class A extends NonBlockingBladeBase {
        public A() throws Exception {
        }

        public AOp<Void> startAOp(final B _b) {
            return new AOp<Void>("start", getReactor()) {
                @Override
                protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                  final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {

                    AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                        @Override
                        public void processAsyncResponse(final Void _response) throws Exception {
                            System.out.println("added 1");
                            _asyncResponseProcessor.processAsyncResponse(null);
                        }
                    };

                    _asyncRequestImpl.send(_b.add1AOp(), startResponse);
                }
            };
        }
    }

    public class M {
        public static void main(final String[] _args) throws Exception {
            new Plant();
            try {
                A a = new A();
                B b = new BImpl();
                a.startAOp(b).call();
            } finally {
                Plant.close();
            }
        }
    }
```

Parallel Processing
-----

So far everything we have looked at, while fully asynchronous, is entirely sequential--doing one thing at a time in
a pre-determined order. But doing things in parallel is as simple as having multiple outstanding sends.

```java

    import org.agilewiki.jactor2.core.impl.Plant;
    import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
    import org.agilewiki.jactor2.core.requests.AOp;
    import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
    import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

    public class AllMain {
        public static void main(final String[] _args) throws Exception {
            new Plant();
            try {
                new All(new A1("1"), new A1("2"), new A1("3")).call();
            } finally {
                Plant.close();
            }
        }
    }

    class All extends AOp<Void> {
        final AOp<Void>[] operations;

        All(final AOp<Void> ... _operations) throws Exception {
            super("All", new NonBlockingReactor());
            operations = _operations;
        }

        @Override
        protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                          final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                throws Exception {

            AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    if (_asyncRequestImpl.getPendingResponseCount() == 0)
                        _asyncResponseProcessor.processAsyncResponse(null);
                }
            };

            int i = 0;
            while (i < operations.length) {
                _asyncRequestImpl.send(operations[i], responseProcessor);
                i += 1;
            }
        }
    }

    class A1 extends AOp<Void> {
        A1(String _name) throws Exception {
            super(_name, new NonBlockingReactor());
        }

        @Override
        protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                          AsyncResponseProcessor<Void> _asyncResponseProcessor)
                throws Exception {
            System.out.println(opName);
            _asyncResponseProcessor.processAsyncResponse(null);
        }
    }
```

In the above example there is no persistent state, which is why there are no blades. Instead we just define
operation classes and in their constructors we create the required reactors.

One new method has been introduced in the responseProcessor, _asyncRequestImpl.getPendingResponseCount(). JActor2 tracks the
number of incomplete subordinate operations and this method returns their count. We use this method to ensure that
all the operations have completed before the All operation returns a null response value.

Advanced Features
=====

JActor2 goes well beyond basic 2-way messaging, providing a comprehensive and robust set of features.

Canceled Operations
-----

Operations are canceled when they are no longer useful and once canceled, an operation can no longer send subordinate
operations. This has the obvious advantage of quickly freeing up
resources being used by operations that are no longer relevant.
There are a number of ways an operation can be canceled:

- When a reactor is closed,
all pending operations sent by that reactor are canceled.
- When an operation completes or an operation is canceled,
all pending operations sent by that operation are canceled.
- The application logic of an operation can explicitly cancel an operation or all of its pending operations.

Of course, there may be some cases where just canceling an operation might corrupt the state of a reactor.
But the application logic has the option of overriding the AOp.onCancel method.

Most cancellations occur when a reactor has outstanding operations and an uncaught RuntimeException occurs.
The exception causes the reactor to close, and the outstanding operations are canceled in turn.
But there are cases when an operation is canceled because it is no longer useful.
Consider a requirement where there multiple alternative operations that can be used. We can process these
operations in parallel and use the first result returned.

```java

    class Any<RESPONSE_TYPE> extends AOp<RESPONSE_TYPE> {
        final AOp<RESPONSE_TYPE>[] operations;

        public Any(final AOp<RESPONSE_TYPE>... _operations) throws Exception {
            super("any", new NonBlockingReactor());
            operations = _operations;
        }

        @Override
        protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                          final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
                throws Exception {
            _asyncRequestImpl.setExceptionHandler(new ExceptionHandler<RESPONSE_TYPE>() {
                @Override
                public void processException(
                        Exception e,
                        AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
                        throws Exception {
                    if (_asyncRequestImpl.getPendingResponseCount() == 0)
                        throw e;
                }
            });

            int i = 0;
            while (i < operations.length) {
                _asyncRequestImpl.send(operations[i], _asyncResponseProcessor); //Send the requests and pass back the first result received
                i += 1;
            }
        }
    }
```

The Any class sends a series of operations, and returns the first response.
An exception from a request is rethrown only if it is from the last outstanding operation.
(If any earlier result is received, the rethrown exception is ignored,
as an operation can pass back only a single result or exception.)

Here then is the rest of the program and its output:

```java

    import org.agilewiki.jactor2.core.impl.Plant;
    import org.agilewiki.jactor2.core.reactors.BlockingReactor;
    import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
    import org.agilewiki.jactor2.core.requests.AOp;
    import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
    import org.agilewiki.jactor2.core.requests.ExceptionHandler;
    import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

    class A2 extends AOp<Long> {
        final long delay;

        A2(final String _name, final long _delay) throws Exception {
            super(_name, new NonBlockingReactor());
            delay = _delay;
        }

        @Override
        protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                          final AsyncResponseProcessor<Long> _asyncResponseProcessor)
                throws Exception {
            for (long i = 0; i < delay * 100000; i++)
                Thread.yield();
            _asyncResponseProcessor.processAsyncResponse(delay);
        }
    }

    class ForcedException extends Exception {}

    class A3 extends AOp<Long> {
        final long delay;

        A3(final String _name, final long _delay) throws Exception {
            super(_name, new BlockingReactor());
            delay = _delay;
        }

        @Override
        protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                          final AsyncResponseProcessor<Long> _asyncResponseProcessor)
                throws Exception {
            if (delay == 0)
                throw new ForcedException();
            for (long i = 0; i < delay * 10000000; i++) {
                if (i % 1000 == 0 && _asyncRequestImpl.isCanceled())
                    return;
                Thread.yield();
            }
            _asyncResponseProcessor.processAsyncResponse(delay);
        }
    }

    public class AnyMain {
        public static void main(final String[] _args) throws Exception {
            new Plant();
            try {
                System.out.println("\ntest 1");
                long x = new Any<Long>(new A2("1", 1), new A2("2", 2), new A2("3", 3)).call();
                System.out.println("got " + x);

                System.out.println("\ntest 2");
                x = new Any<Long>(new A3("1", 1), new A3("2", 2), new A3("3", 0)).call();
                System.out.println("got " + x);

                System.out.println("\ntest 3");
                try {
                    new Any<Long>(new A3("1", 0), new A3("2", 0), new A3("3", 0)).call();
                } catch (ForcedException fe) {
                    System.out.println("Forced Exception");
                }
            } finally {
                Plant.close();
            }
        }
    }
```

````
    test 1
    got 2

    test 2
    got 1

    test 3
    Forced Exception
````

Class A2 represents a typical operation, with no extra code needed to handle a cancellation.
Once the processAsyncOperation method is called, the operation will execute until control is returned,
though it can send no operations once it is canceled.

Class A3 represents a long-running operation. It uses a BlockingReactor rather than a NonBlockingReactor which
changes the default message timeout from 1 second to 5 minutes. A3 also periodically checks to see if it has
been canceled.
(A BlockingReactor should be used when there are operations which tie up a thread, either because of heavy
computation or because of I/O.)

Partial Failure
-----

Reactors can be closed and when they are,
all pending operations sent to them are passed back a
[ReactorClosedException](http://www.agilewiki.org/docs/api/org/agilewiki/jactor2/core/reactors/ReactorClosedException.html).
And once closed, all subsequent operations immediately receive a ReactorClosedException as well.

When JActor detects a problem that can result in corrupted state while an operation is being processed,
the default recovery is to log the problem and close the reactor. Some examples:

- An uncaught RuntimeException is thrown.
- A StackOverflowError is thrown.
- A message takes too long to process.
- A message is processed, no response has been passed back, and there are no pending requests. (Hung request.)

The ReactorClosedException itself is a RuntimeException, and if a operation's ExceptionHandler rethrows
ReactorClosedException, then the operation's reactor is also closed.
So it is important to catch this exception at the points where a partial failure can be handled.

The point here is that any sufficiently large program will have bugs, and isolating a failure to a few reactors
can be very important. The failed reactors can then be optionally restarted.

Message Buffers
-----

There is a fair amount of overhead in passing messages between threads. Send buffers requests and responses, rather
than passing them immediately, to avoid some of this overhead and improve throughput. Message buffers are per destination
reactor and are only disbursed to their destinations when a reactor has no further messages to process.

Thread Migration
-----

Thread migration is another technique used to avoid the overhead of passing messages between threads. Simply put,
when a destination reactor has no assigned thread, instead of passing a message buffer to another thread, the thread
reassigns itself to the destination reactor. This means that the data being passed between reactors is likely
present in the thread's memory cache when the reactor processes it.

OnIdle
-----

Not everything is always about message processing. There are sometimes low-priority tasks that should be done when
a reactor becomes idle. If a reactor is aggregating data, for example, it may need to forward the aggregate when
there are no messages that need processing rather than always waiting until a fixed size has been reached. This
is easily configured by calling a reactor's setIdle method.

Alternative Implementations
=====

So far we have covered only one way of doing things with JActor2. But one size does not fit all.
So JActor offers a number of alternatives to choose from,
including 5 types of reactor, 5 ways to invoke operations and 3 types of operations. Advanced users can, of course,
define new types of reactors, message passing and operations. This is made possible by minimizing coupling
in their implementation.

Reactors
-----

A reactor maintains a queue of unprocessed messages and determines when and in what order they are processed.
A reactor also handles the buffering of outgoing messages and when those messages should be disbursed to their
destinations.

Reactors are individually configurable for initial local queue size, initial buffer size, recovery strategies
and message processing timeouts. The configuration is otherwise inherited from the Plant's internal reactor or
a parent reactor if one is specified when a reactor is created.

1. **NonBlockingReactor** - A non-blocking reactor presumes that all message processing completes quickly.
Buffered messages are disbursed when the input queue is empty.
The default message processing timeout is 1 second.
2. **BlockingReactor** - A blocking reactor is intended for use when messages may take some time to process or
when there is I/O that may block its thread.
Buffered messages are disbursed after processing each message.
The default message processing timeout is 5 minutes.
3. **IsolationReactor** - Similar to a non-blocking reactor, except that each operation is processed to completion
before processing the next operation.
Buffered messages are disbursed after processing each message.
4. **ThreadBoundReactor** - Similar to a non-blocking reactor, except that all message processing is done using
the thread the reactor is bound to, rather than allocating a thread from a common pool.
Thread migration is disabled.
5. **SwingBoundReactor** - Similar to a thread-bound reactor, except that the bound thread is the Swing UI thread.

Operation Invocations
-----

1. **call** - This is a synchronous passing of a 2-way message, as the caller waits for the response. The call
method can only be done from a foreign thread, not a thread bound to a reactor and not a thread from the common
thread pool. A response, or an exception, is assured. If the reactor targeted by the call is closed, a
ReactorClosedException is thrown by the call method.
2. **send with callback** - Send with callback is the only way a 2-way message can be passed between reactors,
with buffering being used when passing both the operation request and the response.
As with call, a response or exception is assured, though in this case the exception is caught by the optional
exception handler. A send with callback can only be invoked within the context of another operation. And if the
invoking operation is canceled, then the subordinate operation is also canceled.
3. **send with no callback** - A send with no callback is one way to pass a 1-way message to a reactor. Like
send with callback, the message is buffered. But any
exception raised while processing the message is simply logged. Send with no callback must be invoked on an
active reactor--it can but need not be invoked within the context of another request. And if the invoking
reactor is closed, there is no effect on the processing of the message.
4. **signal** - Signal is a second way to pass a 1-way message to a reactor. Signal always passes the
message immediately,
never buffered. And the signal method can be called on any thread. Any exception thrown when the message is
processed is simply logged.
5. An operation can directly invoke a SOp on the same reactor using the _requestImpl.syncDirect method.
Messages are not used to pass the operation.

Operation Types
-----

1. **AOp** - An AOp is asynchronous and reusable. Additionally, the AsyncRequest class has onCancel
and onClose methods that can be overridden, though these must be thread-safe methods.
2. **SOp** - SOp is a simplified and synchronous version of AOp. Being synchronous, it can
not send other operations with a callback. Nor does it support exception handlers, as try/catch will suffice. In place
of the processAsyncRequest method, a processSyncRequest method must be overridden, and the result of the
SOp must be returned by this method. Also, a SOp can be invoked using syncDirect.
3. **SAOp** - A SAOp is a subclass of AOp that supports stateful operations. Consequently, SAOp is single-use.

Next Step
=====

A good next step now would be to look at the
[core tutorial](http://www.agilewiki.org/docs/tutorials/core/index.html),
which covers programming with JActor2 step-by-step.



Links
-----

- core (JActor2 API)
    - [JActor2-Core Javadoc pages](http://www.agilewiki.org/docs/api/core/index.html)
    - Dependencies: [slf4j](http://www.slf4j.org/)
- coreMt (Multi-threaded Implementation)
    - [Tutorial](http://www.agilewiki.org/docs/tutorials/core/index.html)
    - [JActor2-CoreMt Javadoc pages](http://www.agilewiki.org/docs/api/coreMt/index.html)
    - Dependencies: core, [guava](https://code.google.com/p/guava-libraries/)
    - benchmark: [repository](https://github.com/skunkiferous/PingPong), [results](http://skunkiferous.github.io/PingPong/)
- [Downloads](http://www.agilewiki.org/downloads)
- [Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
- License: [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
- [JActor2 Logback Appender](https://github.com/cp149/jactor-logger) by Champion
- [JActor](https://github.com/laforge49/JActor) - the predecessor to JActor2
- [Modeling Actors with Locks](http://www.agilewiki.org/docs/tutorials/actors/index.html)
- [Avoiding Actor Deadlock](http://lambda-the-ultimate.org/node/4908)
