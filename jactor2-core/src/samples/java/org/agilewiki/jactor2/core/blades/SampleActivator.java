package org.agilewiki.jactor2.core.blades;


import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class SampleActivator extends BladeBase implements Activator {

    @Override
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                System.out.println("activated!");
                this.processAsyncResponse(null);
            }
        };
    }
}
