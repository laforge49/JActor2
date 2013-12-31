package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class BoundResponseProcessorTest extends TestCase {
    public void test() throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            final Driver driver = new Driver();
            driver.initialize(new NonBlockingReactor());
            assertEquals("Hello world!", driver.doitAReq().call());
        } finally {
            plant.close();
        }
    }
}

class Driver extends NonBlockingBladeBase {
    private AsyncRequest<String> doitReq;

    public AsyncRequest<String> doitAReq() {
        return doitReq;
    }

    @Override
    public void initialize(final NonBlockingReactor _reactor) throws Exception {
        super.initialize(_reactor);

        doitReq = new AsyncRequest<String>(_reactor) {
            AsyncRequest<String> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                setNoHungRequestCheck();
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
