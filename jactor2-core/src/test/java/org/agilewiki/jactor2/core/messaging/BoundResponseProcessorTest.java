package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class BoundResponseProcessorTest extends TestCase {
    public void test() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        try {
            final Driver driver = new Driver();
            driver.initialize(new NonBlockingMessageProcessor(moduleContext));
            assertEquals("Hello world!", driver.doitReq().call());
        } finally {
            moduleContext.close();
        }
    }
}

class Driver extends ActorBase {
    private AsyncRequest<String> doitReq;

    public AsyncRequest<String> doitReq() {
        return doitReq;
    }

    @Override
    public void initialize(final MessageProcessor _messageProcessor) throws Exception {
        super.initialize(_messageProcessor);

        doitReq = new AsyncRequest<String>(_messageProcessor) {
            AsyncRequest<String> dis = this;

            @Override
            public void processRequest()
                    throws Exception {
                final BoundResponseProcessor<String> boundResponseProcessor = new BoundResponseProcessor<String>(
                        Driver.this, dis);
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
