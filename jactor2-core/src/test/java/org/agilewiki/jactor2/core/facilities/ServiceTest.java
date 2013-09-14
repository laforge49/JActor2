package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class ServiceTest extends TestCase {

    public void test() throws Exception {
        Facility testFacility = new Facility();
        Facility clientFacility = new Facility();
        final Facility serverFacility = new Facility();
        try {
            Reactor testReactor = new NonBlockingReactor(testFacility);
            Server server = new Server(new NonBlockingReactor(serverFacility));
            final Client client = new Client(new NonBlockingReactor(clientFacility), server);
            new AsyncRequest<Void>(testReactor) {
                AsyncRequest<Void> dis = this;

                @Override
                public void processAsyncRequest() throws Exception {
                    client.crossAReq().send(getMessageProcessor(), new AsyncResponseProcessor<Boolean>() {
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
            testFacility.close();
            clientFacility.close();
            serverFacility.close();
        }
    }
}

class Client extends BladeBase {

    Server server;

    Client(Reactor reactor, Server _server) throws Exception {
        initialize(reactor);
        server = _server;
    }

    AsyncRequest<Boolean> crossAReq() {
        return new AsyncRequest<Boolean>(getReactor()) {
            AsyncRequest<Boolean> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<Boolean>() {
                    @Override
                    public Boolean processException(Exception exception) throws Exception {
                        if (!(exception instanceof ServiceClosedException)) {
                            throw exception;
                        }
                        return false;
                    }
                });
                server.hangAReq().send(getMessageProcessor(), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void response) throws Exception {
                        dis.processAsyncResponse(true);
                    }
                });
            }
        };
    }
}

class Server extends BladeBase {
    Server(Reactor reactor) throws Exception {
        initialize(reactor);
    }

    AsyncRequest<Void> hangAReq() {
        return new AsyncRequest<Void>(getReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
            }
        };
    }
}
