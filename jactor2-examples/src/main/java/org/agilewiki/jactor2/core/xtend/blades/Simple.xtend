package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase
import org.agilewiki.jactor2.core.impl.Plant
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.xtend.codegen.AReq

class Simple {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            val a = new A();
            a.startAReq().call();
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

    @AReq
    private def start(AsyncRequest<Void> ar) {
    	val b = new B();
    	ar.send(b.add1AReq(),
    		[System.out.println("added 1"); ar.processAsyncResponse(null)]);
    }
}

class B extends NonBlockingBladeBase {
    var int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    @AReq
    private def add1(AsyncRequest<Void> ar) {
    	count = count + 1;
    	ar.processAsyncResponse(null);
    }
}
