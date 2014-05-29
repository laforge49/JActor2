package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

interface BBB {
    def AsyncRequest<Void> newAdd1();
}

class BImpl extends NonBlockingBladeBase implements BBB {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    override AsyncRequest<Void> newAdd1() {
        return new AsyncRequest<Void>(this) {
            int count;

            override void processAsyncRequest() throws Exception {
                count = count + 1;
                processAsyncResponse(null);
            }
        };
    }
}

class AAA extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    def AsyncRequest<Void> newStart(BBB _b) {
        return new AsyncRequest<Void>(this) {
            val dis = this;

            val startResponse = new AsyncResponseProcessor<Void>() {
                override void processAsyncResponse(Void _response) {
                    System.out.println("added 1");
                    dis.processAsyncResponse(null);
                }
            };

            override void processAsyncRequest() throws Exception {
                send(_b.newAdd1(), startResponse);
            }
        };
    }
}

class Decoupled {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            val a = new AAA();
            val b = new BImpl();
            a.newStart(b).call();
        } finally {
            Plant.close();
        }
    }
}
