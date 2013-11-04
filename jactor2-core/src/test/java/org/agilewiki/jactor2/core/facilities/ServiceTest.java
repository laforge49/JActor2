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
        Plant plant = new Plant();
        Facility clientFacility = plant.createFacilityAReq("Client").call();
        final Facility serverFacility = plant.createFacilityAReq("Server").call();
        try {
            testReactor = new NonBlockingReactor(plant);
            Server server = new Server(new NonBlockingReactor(serverFacility));
            final Client client = new Client(new NonBlockingReactor(clientFacility), server);
            new AsyncBladeRequest<Void>() {
                AsyncRequest<Void> dis = this;

                @Override
                protected void processAsyncRequest() throws Exception {
                    send(client.crossAReq(), new AsyncResponseProcessor<Boolean>() {
                        @Override
                        public void processAsyncResponse(Boolean response) throws Exception {
                            assertFalse(response);
                            dis.processAsyncResponse(null);
                        }
                    });
                    serverFacility.close();
                }
            }.call();
        } finally {
            plant.close();
        }
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(testReactor);
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _request        The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(final RequestBase<RESPONSE_TYPE> _request,
                                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(testReactor, _request, _responseProcessor);
    }
}

class Client extends BladeBase {

    Server server;

    Client(Reactor reactor, Server _server) throws Exception {
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
                    public Boolean processException(Exception exception) throws Exception {
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
    Server(Reactor reactor) throws Exception {
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
