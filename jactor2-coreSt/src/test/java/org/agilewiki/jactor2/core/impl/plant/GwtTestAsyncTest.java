package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.JActorStTestPlantConfiguration;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class GwtTestAsyncTest extends BaseGWTTestCase {
    public void testa() throws Exception {
        final JActorStTestPlantConfiguration config = new JActorStTestPlantConfiguration();
        new Plant(new JActorStTestPlantConfiguration());
        try {
            final Async1 async1 = new Async1();
            async1.startAReq().signal();
        } finally {
            Plant.close();
        }
    }
}

class Async1 extends NonBlockingBladeBase {
    Async1() {
        super(new NonBlockingReactor());
    }

    AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                final Async2 async2 = new Async2();
                send(async2.getAReq(), new AsyncResponseProcessor<String>() {
                    @Override
                    public void processAsyncResponse(final String _response)
                            throws Exception {
                        System.out.println(_response);
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}

class Async2 extends NonBlockingBladeBase {
    Async2() {
        super(new NonBlockingReactor());
    }

    AsyncRequest<String> getAReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                this.processAsyncResponse("Hi");
            }
        };
    }
}
