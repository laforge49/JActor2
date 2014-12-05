package org.agilewiki.jactor2.core.isolation;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class HelloWorld extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new HelloWorld();
        System.out.println("(initialized)");
    }

    public HelloWorld() throws Exception {
        run();
    }

    public void run() {
        new AIO("run") {

            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl, AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                System.out.println("Hello world!");
                Plant.close();
            }
        }.signal();
    }
}
