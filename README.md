Actors are Flawed
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

Actors generally implement request/response in two ways, either by blocking the thread until a response is
received or by selecting only the expected response message for processing. Either way, there is the
possibility of a deadlock occurring, depending on the design of the actor to which the request is sent.

The simplest case of deadlock is when two actors each send a request to the other. At this point, neither
actor will process any further messages. A review of each actor in isolation will not reveal any problems,
as this is a case of actor coupling.

Unfortunately, this problem rarely comes up in simple designs or initial implementations. It is only as
the code matures and the complexity increases that deadlocks begin to occur. Supervisors detect such failed
actors and restart them, which in tern gives rise to the increased chance that messages will be lost. So
timeouts are often added, further increasing the complexity of the code and potentially giving rise to an
increasing frequency of deadlocks.

Coupling, as we all know, is a bad thing.

Introducing JActor2
=====

JActor2 is a robust Java framework for composable actors. But before diving into the details, we should
first define a few terms:

- An actor in JActor2 is called **reactor** and is a kind of light-weight thread.
Reactors are extended by adding blades.
- A message is called **request** and is a first class single-use objects.
A request is defined as a class or as an anonymous or nested class within a blade.
Requests are bound to a reactor and are evaluated (executed)
only on the reactor's thread.
After a request has been evaluated and has a result, it becomes a response
and is passed back to the reactor which originated the request.
- When a request is sent by one actor to another actor, a callback is assigned to the request.
The callback is a subclass of **AsyncResponseProcessor** and has a single method, processAsyncResponse.
And when a response is passed back to the originating reactor, this method is called on the thread of
the originating actor.
- A **blade** has state and a reference to the reactor it is a part of.
A blade defines the requests which operate on its state.
Blades (and requests) can also directly call methods on other blades that are part of the same reactor.

Request/Response
-----

A basic request/response is fairly simple. Let use say that actor A wants to send a request to Actor B.

1. Actor A first creates a request that is bound to actor B and sends it,
together with an AsyncResponseProcessor.
2. The AsyncResponseProcessor object is saved in the request when it is sent. The request is added to
actor B's input queue.
3. Actor B evaluates the request after processing all other messages received before it,
updates the actor's state and then calls the processAsyncRequest method with the response value.
4. When the procesAsyncRequest method is called on the request, it saves the response value and
adds the request (now a response) to the input queue of actor A.
5. After processing all other messages received before it, actor A processes the request by calling the
processAsyncResponse method on the AsyncResponseProcessor object that was assigned to that message.

```java

    class A extends NonBlockingBladeBase {
        class Start extends AsyncBladeRequest<Void> {
            B b = new B();

            AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    System.out.println("added 1");
                    Start.this.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() {
                send(b.new Add1(), startResponse);
            }
        }
    }

    class B extends NonBlockingBladeBase {
        private int count;

        class Add1 extends AsyncBladeRequest<Void> {

            @Override
            public void processAsyncRequest() {
                count += 1;
                processAsyncResponse(null);
            }
        }
    }
```

Things get just a bit more interesting when actor B wants to send a request to actor C
while processing the request from actor A. The problem is that while the response from actor C is pending,
actor C will continue to receive and process other messages. Fortunately requests are single-use first class
objects. So any intermediate results can be saved in the request's own member variables and
are available when the response from actor C is received.

```java

    class A extends NonBlockingBladeBase {
        class Start extends AsyncBladeRequest<Void> {
            B b = new B();

            AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) {
                    System.out.println("added value");
                    Start.this.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() {
                send(b.new AddValue(), startResponse);
            }
        }
    }

    class B extends NonBlockingBladeBase {
        private C c = new C();
        private int count;

        class AddValue extends AsyncBladeRequest<Void> {

            AsyncResponseProcessor<Integer> valueResponse = new AsyncResponseProcessor<Integer>() {
                @Override
                public void processAsyncResponse(Integer _response) {
                   count += _response;
                   AddValue.this.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() {
                send(c.new Value(), valueResponse);
            }
        }
    }

    class C extends NonBlockingBladeBase {
        class Value extends AsyncBladeRequest<Integer> {
            @Override
            public void processAsyncRequest() {
                processAsyncResponse(42);
            }
        }
    }
```

Results are Guaranteed
-----

JActor2 models operations/messages on Java method calls. Operations are implemented using 2-way messaging,
with the result being returned via a callback. And like a method call, an operation will either return a
normal result or an exception.

Operations are monitored to make sure they are behaving as expected and if not,
corrective action is taken and an exception is returned. So if there is a stack overflow error,
a runtime exception, or even if the operation takes too long to process, the situation is managed and
an exception is returned on the thread which sent the operation.

Links
-----

- [Core Tutorial](http://laforge49.github.io/JActor2/docs/tutorials/core/index.html) beta
- [API](http://laforge49.github.io/JActor2/docs/api/index.html)
- [Downloads](http://laforge49.github.io/JActor2/downloads)
- Dependencies: lsf4j, guava
- [Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
- License: [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
