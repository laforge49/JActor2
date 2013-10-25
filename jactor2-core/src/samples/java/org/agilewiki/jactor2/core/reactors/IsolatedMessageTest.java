package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

public class IsolatedMessageTest extends BladeBase {
    public static void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            new IsolatedMessageTest(new IsolationReactor(plant)).startAReq().call();
        } finally {
            plant.close();
        }
    }

    IsolatedMessageTest(final IsolationReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                send(doAReq(), dis);
            }
        };
    }

    AsyncRequest<Void> doAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                dis.processAsyncResponse(null);
            }
        };
    }
}
