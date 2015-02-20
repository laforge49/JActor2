package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

interface BBB {
    AOp<Void> add1AOp();
}

class BImpl extends NonBlockingBladeBase implements BBB {
    int count;

    public BImpl() throws Exception {
    }

    @Override
    public AOp<Void> add1AOp() {
        return new AOp<Void>("add1", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                count = count + 1;
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}

class AAA extends NonBlockingBladeBase {
    public AAA() throws Exception {
    }

    public AOp<Void> startAOp(final BBB _b) {
        return new AOp<Void>("start", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {

                AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) throws Exception {
                        System.out.println("added 1");
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };

                _asyncRequestImpl.send(_b.add1AOp(), startResponse);
            }
        };
    }
}

public class Decoupled {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final AAA a = new AAA();
            final BBB b = new BImpl();
            a.startAOp(b).call();
        } finally {
            Plant.close();
        }
    }
}
