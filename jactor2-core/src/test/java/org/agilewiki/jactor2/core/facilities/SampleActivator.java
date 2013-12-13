package org.agilewiki.jactor2.core.facilities;


import org.agilewiki.jactor2.core.blades.Activator;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class SampleActivator extends BladeBase implements Activator {

    @Override
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                System.out.println("activated: "+getReactor().getFacility().name);
                this.processAsyncResponse(null);
            }
        };
    }
}