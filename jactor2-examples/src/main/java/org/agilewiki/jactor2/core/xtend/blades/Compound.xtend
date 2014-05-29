package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase
import org.agilewiki.jactor2.core.impl.Plant
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.xtend.codegen.AReq

class Compound {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            new AA().startAReq().call();
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

	@AReq
    private def start(AsyncRequest<Void> ar) {
    	val b = new BB();
    	ar.send(b.addValueAReq(b),
    		[System.out.println("added value"); ar.processAsyncResponse(null)]);
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

	@AReq
    private def addValue(AsyncRequest<Void> ar, BB bb) {
        ar.send(bb.c.valueAReq(),
        	[r|bb.count = bb.count + r; ar.processAsyncResponse(null)]);
    }
}

class CC extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

	@AReq
    private def value(AsyncRequest<Integer> ar) {
    	ar.processAsyncResponse(42);
    }
}
