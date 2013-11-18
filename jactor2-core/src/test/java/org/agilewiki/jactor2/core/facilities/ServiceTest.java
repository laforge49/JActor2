package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class ServiceTest extends TestCase {
    Reactor testReactor;

    public void test() throws Exception {
        System.out.println("disabled until there is a single thread pool");
        /*
        final Plant plant = new Plant();
        final Facility clientFacility = plant.createFacilityAReq("Client")
                .call();
        final Facility serverFacility = plant.createFacilityAReq("Server")
                .call();
        try {
            testReactor = new NonBlockingReactor(plant);
            final Server server = new Server(new NonBlockingReactor(
                    serverFacility));
            final Client client = new Client(new NonBlockingReactor(
                    clientFacility), server);
            new AsyncRequest<Void>(testReactor) {
                AsyncRequest<Void> dis = this;

                @Override
                protected void processAsyncRequest() throws Exception {
                    send(client.crossAReq(),
                            new AsyncResponseProcessor<Boolean>() {
                                @Override
                                public void processAsyncResponse(
                                        final Boolean response)
                                        throws Exception {
                                    assertFalse(response);
                                    dis.processAsyncResponse(null);
                                }
                            });
                    serverFacility.close();
                }
            }.call();
        } finally {
            System.out.println("close plant");
            plant.close();
        }
        */
    }
}

class Client extends BladeBase {

    Server server;

    Client(final Reactor reactor, final Server _server) throws Exception {
        initialize(reactor);
        server = _server;
    }

    AsyncRequest<Boolean> crossAReq() {
        return new AsyncBladeRequest<Boolean>() {
            AsyncRequest<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<Boolean>() {
                    @Override
                    public Boolean processException(final Exception exception)
                            throws Exception {
                        if (!(exception instanceof ServiceClosedException)) {
                            throw exception;
                        }
                        return false;
                    }
                });
                send(server.hangAReq(), dis, true);
            }
        };
    }
}

class Server extends BladeBase {
    Server(final Reactor reactor) throws Exception {
        initialize(reactor);
    }

    AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
            }
        };
    }
}
