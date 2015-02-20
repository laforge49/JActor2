package org.agilewiki.jactor2.core.readme.requests;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

import java.io.IOException;

public class ExceptionSample {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            A a = new A();
            a.startAOp().call();
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

    AOp<Void> startAOp() {
        return new AOp<Void>("start", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                B b = new B();

                AsyncResponseProcessor<Void> woopsResponse = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        System.out.println("can not get here!");
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };

                ExceptionHandler<Void> exceptionHandler = new ExceptionHandler<Void>() {
                    @Override
                    public void processException(final Exception _e,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                            throws Exception {
                        if (_e instanceof IOException) {
                            System.out.println("got IOException");
                            _asyncResponseProcessor.processAsyncResponse(null);
                        } else
                            throw _e;
                    }
                };

                _asyncRequestImpl.setExceptionHandler(exceptionHandler);
                _asyncRequestImpl.send(b.woopsAOp(), woopsResponse);
            }
        };
    }
}

class B extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public B() throws Exception {
    }

    AOp<Void> woopsAOp() {
        return new AOp<Void>("woops", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                throw new IOException();
            }
        };
    }
}
