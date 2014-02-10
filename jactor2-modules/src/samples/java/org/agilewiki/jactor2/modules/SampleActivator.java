package org.agilewiki.jactor2.modules;


import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class SampleActivator extends Activator {

    public SampleActivator(NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    @Override
    public AsyncRequest<Void> startAReq() {
        return new BladeBase.AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() {
                System.out.println("activated!");
                this.processAsyncResponse(null);
            }
        };
    }
}
