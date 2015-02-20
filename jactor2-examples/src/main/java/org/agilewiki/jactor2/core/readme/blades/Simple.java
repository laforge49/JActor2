package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Simple {
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
    final B b;

    public A() throws Exception {
        b = new B();
    }

    AOp<Void> startAOp() {
        return new AOp<Void>("start", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        System.out.println("added 1");
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };
                _asyncRequestImpl.send(b.add1AOp(), startResponse);
            }
        };
    }
}

class B extends NonBlockingBladeBase {
    private int count;

    public B() throws Exception {
    }

    AOp<Void> add1AOp() {
        return new AOp<Void>("add1", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                count += 1;
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
