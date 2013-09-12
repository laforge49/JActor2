package org.agilewiki.jactor2.core;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.ServiceClosedException;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

public class ServiceTest extends TestCase {

    public void test() throws Exception {
        Facility testFacility = new Facility();
        Facility clientFacility = new Facility();
        final Facility serverFacility = new Facility();
        try {
            MessageProcessor testMessageProcessor = new NonBlockingMessageProcessor(testFacility);
            Server server = new Server(new NonBlockingMessageProcessor(serverFacility));
            final Client client = new Client(new NonBlockingMessageProcessor(clientFacility), server);
            new AsyncRequest<Void>(testMessageProcessor) {
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

class Client extends ActorBase {

    Server server;

    Client(MessageProcessor messageProcessor, Server _server) throws Exception {
        initialize(messageProcessor);
        server = _server;
    }

    AsyncRequest<Boolean> crossAReq() {
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
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

class Server extends ActorBase {
    Server(MessageProcessor messageProcessor) throws Exception {
        initialize(messageProcessor);
    }

    AsyncRequest<Void> hangAReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
            }
        };
    }
}
