package org.agilewiki.jactor.general;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

public class ServiceTest extends TestCase {

    public void test() throws Exception {
        MailboxFactory testMBF = new DefaultMailboxFactoryImpl();
        MailboxFactory clientMBF = new DefaultMailboxFactoryImpl();
        final MailboxFactory serverMBF = new DefaultMailboxFactoryImpl();
        try {
            Mailbox testMailbox = testMBF.createMailbox();
            Server server = new Server(serverMBF.createMailbox());
            final Client client = new Client(clientMBF.createMailbox(), server);
            new RequestBase<Void>(testMailbox) {
                @Override
                public void processRequest(final Transport<Void> _transport) throws Exception {
                    client.crossReq().send(getMailbox(), new ResponseProcessor<Boolean>() {
                        @Override
                        public void processResponse(Boolean response) throws Exception {
                            assertFalse(response);
                            _transport.processResponse(null);
                        }
                    });
                    serverMBF.close();
                }
            }.call();
        } finally {
            testMBF.close();
            clientMBF.close();
            serverMBF.close();
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
        return new RequestBase<Boolean>(getMailbox()) {
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
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
            }
        };
    }
}
