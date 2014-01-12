package org.agilewiki.jactor2.modules.facilities;


import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.modules.Activator;
import org.agilewiki.jactor2.modules.Facility;

public class SampleActivator extends Activator {

    public SampleActivator(NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    @Override
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                System.out.println("activated: "+((Facility)getReactor()).getName());
                this.processAsyncResponse(null);
            }
        };
    }
}
