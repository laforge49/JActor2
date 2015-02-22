package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SIOp;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class OneWayError extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public OneWayError() throws Exception {
    }

    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            new OneRuntime().new OneWaySOp("direct", new NonBlockingReactor()).signal();
            new OneWayError().new IndirectAOp("indirect", new NonBlockingReactor()).call();
            System.out.println("ok");
        } finally {
            Plant.close();
        }
    }

    public class IndirectAOp extends AOp<Void> {
        public IndirectAOp(String _opName, Reactor _targetReactor) {
            super(_opName, _targetReactor);
        }

        @Override
        protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                          AsyncResponseProcessor<Void> _asyncResponseProcessor)
                throws Exception {
            _asyncRequestImpl.send(new OneRuntime().new OneWaySOp("oneway", getReactor()), null);
            _asyncResponseProcessor.processAsyncResponse(null);
        }
    }
}

class OneRuntime extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public OneRuntime() throws Exception {
    }

    public class OneWaySOp extends SIOp<Void> {
        public OneWaySOp(String _opName, Reactor _targetReactor) {
            super(_opName, _targetReactor);
        }

        @Override
        protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
            throw new RuntimeException();
        }
    }
}
