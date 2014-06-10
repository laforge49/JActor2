package org.agilewiki.jactor2.core.xtend.requests;

import java.io.IOException;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.xtend.codegen.AReq

class ExceptionSample {
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
    private def _start(AsyncRequest<Void> ar) {
    	val b = new B();
        val exceptionHandler = new ExceptionHandler<Void>() {
            override void processException(Exception _e,
                    AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                if (_e instanceof IOException) {
                    System.out.println("got IOException");
                    _asyncResponseProcessor.processAsyncResponse(null);
                } else
                    throw _e;
            }
        };
        ar.setExceptionHandler(exceptionHandler);
        ar.send(b.woopsAReq(),
        	[System.out.println("can not get here!"); ar.processAsyncResponse(null)]);
    }
}

class B extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    @AReq
    private def _woops(AsyncRequest<Void> ar) throws IOException {
    	throw new IOException();
    }
}
