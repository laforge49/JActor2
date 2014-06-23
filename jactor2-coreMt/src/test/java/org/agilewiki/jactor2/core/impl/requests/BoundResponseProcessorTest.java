package org.agilewiki.jactor2.core.impl.requests;

import junit.framework.Assert;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.BoundResponseProcessor;

public class BoundResponseProcessorTest extends CallTestBase {
    public void test() throws Exception {
        new Plant();
        try {
            final Driver driver = new Driver();
            Assert.assertEquals("Hello world!", call(driver.doitAOp()));
        } finally {
            Plant.close();
        }
    }
}

class Driver extends NonBlockingBladeBase {
    private final AOp<String> doitAOp;

    public Driver() throws Exception {
        doitAOp = new AOp<String>("doit", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequest _asyncRequest,
                                               final AsyncResponseProcessor<String> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequest.setNoHungRequestCheck();
                final BoundResponseProcessor<String> boundResponseProcessor = new BoundResponseProcessor<String>(
                        Driver.this, _asyncResponseProcessor);
                final Application application = new Application(
                        boundResponseProcessor);
                application.start();
            }
        };
    }

    public AOp<String> doitAOp() {
        return doitAOp;
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
