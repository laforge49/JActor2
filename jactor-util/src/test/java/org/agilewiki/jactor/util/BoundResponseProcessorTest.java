package org.agilewiki.jactor.util;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.*;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

public class BoundResponseProcessorTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            final Driver driver = new Driver();
            driver.initialize(mailboxFactory.createMailbox());
            assertEquals("Hello world!", driver.doitReq().call());
        } finally {
            mailboxFactory.close();
        }
    }
}

class Driver extends ActorBase {
    private Request<String> doitReq;

    public Request<String> doitReq() {
        return doitReq;
    }

    @Override
    public void initialize(final Mailbox _mailbox) throws Exception {
        super.initialize(_mailbox);

        doitReq = new RequestBase<String>(_mailbox) {
            @Override
            public void processRequest(final Transport<String> rp)
                    throws Exception {
                final BoundResponseProcessor<String> boundResponseProcessor = new BoundResponseProcessor<String>(
                        _mailbox, rp);
                final Application application = new Application(
                        boundResponseProcessor);
                application.start();
            }
        };
    }
}

class Application extends Thread {
    private final BoundResponseProcessor<String> boundResponseProcessor;

    public Application(
            final BoundResponseProcessor<String> _boundResponseProcessor) {
        boundResponseProcessor = _boundResponseProcessor;
    }

    @Override
    public void run() {
        try {
            boundResponseProcessor.processResponse("Hello world!");
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
    }
}
