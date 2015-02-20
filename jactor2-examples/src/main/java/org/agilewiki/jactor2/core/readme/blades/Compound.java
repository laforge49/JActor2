package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Compound {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final AA a = new AA();
            a.startAOp().call();
        } finally {
            Plant.close();
        }
    }
}

class AA extends NonBlockingBladeBase {
    public AA() throws Exception {
    }

    AOp<Void> startAOp() {
        return new AOp<Void>("start", getReactor()) {

            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {

                AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) throws Exception {
                        System.out.println("added value");
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };

                BB b = new BB();
                _asyncRequestImpl.send(b.addValueAOp(), startResponse);
            }
        };
    }
}

class BB extends NonBlockingBladeBase {
    private final CC c = new CC();
    private int count;

    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    BB() throws Exception {
    }

    AOp<Void> addValueAOp() {
        return new AOp<Void>("addValue", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {

                AsyncResponseProcessor<Integer> valueResponse = new AsyncResponseProcessor<Integer>() {
                    @Override
                    public void processAsyncResponse(final Integer _response)
                            throws Exception {
                        count += _response;
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };

                _asyncRequestImpl.send(c.valueAOp(), valueResponse);
            }
        };
    }
}

class CC extends NonBlockingBladeBase {
    CC() throws Exception {
    }

    AOp<Integer> valueAOp() {
        return new AOp<Integer>("value", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Integer> _asyncResponseProcessor)
                    throws Exception {
                _asyncResponseProcessor.processAsyncResponse(42);
            }
        };
    };
}
