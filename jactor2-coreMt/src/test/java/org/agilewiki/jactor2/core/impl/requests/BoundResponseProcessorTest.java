package org.agilewiki.jactor2.core.impl.requests;

import junit.framework.Assert;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.BoundResponseProcessor;

public class BoundResponseProcessorTest extends CallTestBase {
    public void test() throws Exception {
        new Plant();
        try {
            final Driver driver = new Driver();
            Assert.assertEquals("Hello world!", call(driver.doitAReq()));
        } finally {
            Plant.close();
        }
    }
}

class Driver extends NonBlockingBladeBase {
    private final AsyncRequest<String> doitReq;

    public Driver() throws Exception {
        doitReq = new AsyncBladeRequest<String>() {
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

    public AsyncRequest<String> doitAReq() {
        return doitReq;
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
