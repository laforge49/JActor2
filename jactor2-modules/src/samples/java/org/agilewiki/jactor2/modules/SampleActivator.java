package org.agilewiki.jactor2.modules;


import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class SampleActivator extends NonBlockingBladeBase implements Activator {

    @Override
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                System.out.println("activated!");
                this.processAsyncResponse(null);
            }
        };
    }
}
