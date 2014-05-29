package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.AsyncRequest

class Compound {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            new AA().newStart().call();
        } finally {
            Plant.close();
        }
    }
}

class AA extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class Start extends AsyncRequest<Void> {
        val b = new BB();

        val startResponse = new AsyncResponseProcessor<Void>() {
            override void processAsyncResponse(Void _response) {
                System.out.println("added value");
                Start.this.processAsyncResponse(null);
            }
        };

        new(AA aa) throws Exception {
        	super(aa);
        }

        override void processAsyncRequest() {
            send(b.newAddValue(), startResponse);
        }
    }

    def newStart() {
    	new Start(this)
    }
}

class BB extends NonBlockingBladeBase {
    val c = new CC();
    var int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class AddValue extends AsyncRequest<Void> {
    	val BB bb;

		new (BB bb) {
			super(bb)
			this.bb = bb;
		}

        val valueResponse = new AsyncResponseProcessor<Integer>() {
            override void processAsyncResponse(Integer _response) {
                bb.count = bb.count + _response;
                AddValue.this.processAsyncResponse(null);
            }
        };

        override void processAsyncRequest() {
            send(bb.c.newValue(), valueResponse);
        }
    }

    def newAddValue() {
    	new AddValue(this)
    }
}

class CC extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class Value extends AsyncRequest<Integer> {
    	new (CC cc) {
    		super(cc)
    	}
        override void processAsyncRequest() {
            processAsyncResponse(42);
        }
    }

    def newValue() {
    	new Value(this)
    }
}
