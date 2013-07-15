package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.*;

public class BoundResponseProcessorTest extends TestCase {
    public void test() throws Exception {
        final UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        try {
            final Driver driver = new Driver();
            driver.initialize(mailboxFactory.createNonBlockingMailbox());
            assertEquals("Hello world!", driver.doitReq().call());
        } finally {
            mailboxFactory.close();
        }
    }
}

class Driver extends ActorBase {
    private BoundRequest<String> doitReq;

    public BoundRequest<String> doitReq() {
        return doitReq;
    }

    @Override
    public void initialize(final Mailbox _mailbox) throws Exception {
        super.initialize(_mailbox);

        doitReq = new BoundRequestBase<String>(_mailbox) {
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
