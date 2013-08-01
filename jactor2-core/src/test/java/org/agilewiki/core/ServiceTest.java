package org.agilewiki.core;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;
import org.agilewiki.jactor2.core.messaging.*;

public class ServiceTest extends TestCase {

    public void test() throws Exception {
        JAContext testContext = new JAContext();
        JAContext clientContext = new JAContext();
        final JAContext serverContext = new JAContext();
        try {
            Mailbox testMailbox = new NonBlockingMailbox(testContext);
            Server server = new Server(new NonBlockingMailbox(serverContext));
            final Client client = new Client(new NonBlockingMailbox(clientContext), server);
            new Request<Void>(testMailbox) {
                @Override
                public void processRequest(final Transport<Void> _transport) throws Exception {
                    client.crossReq().send(getMailbox(), new ResponseProcessor<Boolean>() {
                        @Override
                        public void processResponse(Boolean response) throws Exception {
                            assertFalse(response);
                            _transport.processResponse(null);
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

    Client(Mailbox mailbox, Server _server) throws Exception {
        initialize(mailbox);
        server = _server;
    }

    Request<Boolean> crossReq() {
        return new Request<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Boolean> _transport) throws Exception {
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (!(throwable instanceof ServiceClosedException)) {
                            throw throwable;
                        }
                        _transport.processResponse(false);
                    }
                });
                server.hangReq().send(getMailbox(), new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Exception {
                        _transport.processResponse(true);
                    }
                });
            }
        };
    }
}

class Server extends ActorBase {
    Server(Mailbox mailbox) throws Exception {
        initialize(mailbox);
    }

    Request<Void> hangReq() {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
            }
        };
    }
}
