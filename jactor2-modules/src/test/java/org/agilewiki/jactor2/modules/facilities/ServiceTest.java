package org.agilewiki.jactor2.modules.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.MPlant;

public class ServiceTest extends TestCase {
    public void test() throws Exception {
        new MPlant();
        final Facility clientFacility = MPlant.createFacilityAReq("Client")
                .call();
        final Facility serverFacility = MPlant.createFacilityAReq("Server")
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
            Plant.close();
        }
    }
}

class Client extends NonBlockingBladeBase {

    Server server;

    Client(final NonBlockingReactor reactor, final Server _server) throws Exception {
        super(reactor);
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
        super(reactor);
    }

    AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
            }
        };
    }
}
