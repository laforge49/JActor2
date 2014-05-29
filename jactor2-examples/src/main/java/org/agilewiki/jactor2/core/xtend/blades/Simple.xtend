package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.AsyncRequest

class Simple {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            val a = new A();
            a.newStart().call();
        } finally {
            Plant.close();
        }
    }
}

class A extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class Start extends AsyncRequest<Void> {
        val b = new B();

        val startResponse = new AsyncResponseProcessor<Void>() {
            override void processAsyncResponse(Void _response) {
                System.out.println("added 1");
                Start.this.processAsyncResponse(null);
            }
        };

        new(A a) throws Exception {
            super(a);
        }

        override void processAsyncRequest() {
            send(b.newAdd1(), startResponse);
        }
    }

    def newStart() {
    	new Start(this)
    }
}

class B extends NonBlockingBladeBase {
    var int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class Add1 extends AsyncRequest<Void> {
    	val B b
    	new (B b) {
    		super(b);
    		this.b = b
    	}

        override void processAsyncRequest() {
            b.count = b.count + 1;
            processAsyncResponse(null);
        }
    }

    def newAdd1() {
    	new Add1(this)
    }
}
