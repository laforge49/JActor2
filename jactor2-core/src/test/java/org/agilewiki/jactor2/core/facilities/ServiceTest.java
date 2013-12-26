package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.Request;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ServiceTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = new Plant();
        final Facility clientFacility = plant.createFacilityAReq("Client")
                .call();
        final Facility serverFacility = plant.createFacilityAReq("Server")
                .call();
        try {
            NonBlockingReactor serverReactor = new NonBlockingReactor(serverFacility);
            final Server server = new Server(serverReactor);
            NonBlockingReactor clientReactor = new NonBlockingReactor(clientFacility);
            final Client client = new Client(clientReactor, server);
            NonBlockingReactor testReactor = new NonBlockingReactor();
            new AsyncRequest<Void>(testReactor) {
                AsyncRequest<Void> dis = this;

                @Override
                public void processAsyncRequest() throws Exception {
                    send(client.crossAReq(),
                            new AsyncResponseProcessor<Boolean>() {
                                @Override
                                public void processAsyncResponse(
                                        final Boolean response)
                                        throws Exception {
                                    Thread.sleep(10);
                                    System.out.println("Bingo!");
                                    Thread.sleep(10);
                                    assertFalse(response);
                                    dis.processAsyncResponse(null);
                                }
                            });
                }
            }.signal();
            //serverReactor.close();     //this works
            serverFacility.close();  //this also works
        } finally {
            plant.close();
        }
    }
}

class Client extends NonBlockingBladeBase {

    Server server;

    Client(final NonBlockingReactor reactor, final Server _server) throws Exception {
        initialize(reactor);
        server = _server;
    }

    AsyncRequest<Boolean> crossAReq() {
        return new AsyncBladeRequest<Boolean>() {
            AsyncRequest<Boolean> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<Boolean>() {
                    @Override
                    public Boolean processException(final Exception exception)
                            throws Exception {
                        System.out.println("client got exception");
                        if (!(exception instanceof ServiceClosedException)) {
                            throw exception;
                        }
                        return false;
                    }
                });
                Thread.sleep(10);
                Request rb = server.hangAReq();
                System.out.println("client send hang "+rb);
                Thread.sleep(10);
                send(rb, dis, true);
            }
        };
    }
}

class Server extends NonBlockingBladeBase {
    Server(final NonBlockingReactor reactor) throws Exception {
        initialize(reactor);
    }

    AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
            }
        };
    }
}
