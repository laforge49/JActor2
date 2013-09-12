package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.BladeBase;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class BoundResponseProcessorTest extends TestCase {
    public void test() throws Exception {
        final Facility facility = new Facility();
        try {
            final Driver driver = new Driver();
            driver.initialize(new NonBlockingReactor(facility));
            assertEquals("Hello world!", driver.doitAReq().call());
        } finally {
            facility.close();
        }
    }
}

class Driver extends BladeBase {
    private AsyncRequest<String> doitReq;

    public AsyncRequest<String> doitAReq() {
        return doitReq;
    }

    @Override
    public void initialize(final Reactor _reactor) throws Exception {
        super.initialize(_reactor);

        doitReq = new AsyncRequest<String>(_reactor) {
            AsyncRequest<String> dis = this;

            @Override
            public void processAsyncRequest()
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
            boundResponseProcessor.processAsyncResponse("Hello world!");
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
    }
}
