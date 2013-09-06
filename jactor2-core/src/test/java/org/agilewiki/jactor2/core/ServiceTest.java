package org.agilewiki.jactor2.core;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ServiceClosedException;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ServiceTest extends TestCase {

    public void test() throws Exception {
        ModuleContext testContext = new ModuleContext();
        ModuleContext clientContext = new ModuleContext();
        final ModuleContext serverContext = new ModuleContext();
        try {
            MessageProcessor testMessageProcessor = new NonBlockingMessageProcessor(testContext);
            Server server = new Server(new NonBlockingMessageProcessor(serverContext));
            final Client client = new Client(new NonBlockingMessageProcessor(clientContext), server);
            new Request<Void>(testMessageProcessor) {
                Request<Void> dis = this;

                @Override
                public void processRequest() throws Exception {
                    client.crossReq().send(getMessageProcessor(), new ResponseProcessor<Boolean>() {
                        @Override
                        public void processResponse(Boolean response) throws Exception {
                            assertFalse(response);
                            dis.processResponse(null);
                        }
                    });
                    serverContext.close();
                }
            }.call();
        } finally {
            testContext.close();
            clientContext.close();
            serverContext.close();
        }
    }
}

class Client extends ActorBase {

    Server server;

    Client(MessageProcessor messageProcessor, Server _server) throws Exception {
        initialize(messageProcessor);
        server = _server;
    }

    Request<Boolean> crossReq() {
        return new Request<Boolean>(getMessageProcessor()) {
            Request<Boolean> dis = this;

            @Override
            public void processRequest() throws Exception {
                getMessageProcessor().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (!(throwable instanceof ServiceClosedException)) {
                            throw throwable;
                        }
                        processResponse(false);
                    }
                });
                server.hangReq().send(getMessageProcessor(), new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Exception {
                        dis.processResponse(true);
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

    Request<Void> hangReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
            }
        };
    }
}
