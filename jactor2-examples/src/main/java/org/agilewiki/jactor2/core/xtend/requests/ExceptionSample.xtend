package org.agilewiki.jactor2.core.xtend.requests;

import java.io.IOException;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.AsyncRequest

class ExceptionSample {
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

        val woopsResponse = new AsyncResponseProcessor<Void>() {
            override void processAsyncResponse(Void _response) {
                System.out.println("can not get here!");
                Start.this.processAsyncResponse(null);
            }
        };

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

        new(A a) throws Exception {
            super(a);
        }

        override void processAsyncRequest() {
            setExceptionHandler(exceptionHandler);
            send(b.newWoops(), woopsResponse);
        }
    }

    def newStart() {
    	new Start(this)
    }
}

class B extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    new() throws Exception {
    }

    static class Woops extends AsyncRequest<Void> {
		new(B b) {
			super(b)
		}

        override void processAsyncRequest() throws IOException {
            throw new IOException();
        }
    }

    def newWoops() {
    	new Woops(this)
    }
}
