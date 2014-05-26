package org.agilewiki.jactor2.core.examples.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Simple {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            A a = new A();
            a.new Start().call();
        } finally {
            Plant.close();
        }
    }
}

class A extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public A() throws Exception {
    }

    class Start extends AsyncBladeRequest<Void> {
        B b;

        AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) {
                System.out.println("added 1");
                Start.this.processAsyncResponse(null);
            }
        };

        Start() throws Exception {
            b = new B();
        }

        @Override
        public void processAsyncRequest() {
            send(b.new Add1(), startResponse);
        }
    }
}

class B extends NonBlockingBladeBase {
    private int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public B() throws Exception {
    }

    class Add1 extends AsyncBladeRequest<Void> {

        @Override
        public void processAsyncRequest() {
            count += 1;
            processAsyncResponse(null);
        }
    }
}
