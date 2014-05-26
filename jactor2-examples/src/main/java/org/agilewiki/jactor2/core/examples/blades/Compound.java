package org.agilewiki.jactor2.core.examples.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Compound {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            AA a = new AA();
            a.new Start().call();
        } finally {
            Plant.close();
        }
    }
}

class AA extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public AA() throws Exception {
    }

    class Start extends AsyncBladeRequest<Void> {
        BB b;

        AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) {
                System.out.println("added value");
                Start.this.processAsyncResponse(null);
            }
        };

        Start() throws Exception {
            b = new BB();
        }

        @Override
        public void processAsyncRequest() {
            send(b.new AddValue(), startResponse);
        }
    }
}

class BB extends NonBlockingBladeBase {
    private CC c = new CC();
    private int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public BB() throws Exception {
    }

    class AddValue extends AsyncBladeRequest<Void> {

        AsyncResponseProcessor<Integer> valueResponse = new AsyncResponseProcessor<Integer>() {
            @Override
            public void processAsyncResponse(Integer _response) {
                count += _response;
                AddValue.this.processAsyncResponse(null);
            }
        };

        @Override
        public void processAsyncRequest() {
            send(c.new Value(), valueResponse);
        }
    }
}

class CC extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public CC() throws Exception {
    }

    class Value extends AsyncBladeRequest<Integer> {
        @Override
        public void processAsyncRequest() {
            processAsyncResponse(42);
        }
    }
}
