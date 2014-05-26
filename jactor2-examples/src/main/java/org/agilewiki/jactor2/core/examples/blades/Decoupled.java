package org.agilewiki.jactor2.core.examples.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

interface BBB {
    AsyncRequest<Void> newAdd1();
}

class BImpl extends NonBlockingBladeBase implements BBB {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public BImpl() throws Exception {
    }

    @Override
    public AsyncRequest<Void> newAdd1() {
        return new AsyncBladeRequest<Void>() {
            int count;

            @Override
            public void processAsyncRequest() throws Exception {
                count += 1;
                processAsyncResponse(null);
            }
        };
    }
}

class AAA extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public AAA() throws Exception {
    }

    public AsyncRequest<Void> newStart(final BBB _b) {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) {
                    System.out.println("added 1");
                    dis.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                send(_b.newAdd1(), startResponse);
            }
        };
    }
}

public class Decoupled {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            AAA a = new AAA();
            BBB b = new BImpl();
            a.newStart(b).call();
        } finally {
            Plant.close();
        }
    }
}
