package org.agilewiki.jactor2.core.impl.blades;

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
    class Start extends AsyncBladeRequest<Void> {
        B b = new B();

        AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) {
                System.out.println("added 1");
                Start.this.processAsyncResponse(null);
            }
        };

        @Override
        public void processAsyncRequest() {
            send(b.new Add1(), startResponse);
        }
    }
}

class B extends NonBlockingBladeBase {
    private int count;

    class Add1 extends AsyncBladeRequest<Void> {

        @Override
        public void processAsyncRequest() {
            count += 1;
            processAsyncResponse(null);
        }
    }
}
