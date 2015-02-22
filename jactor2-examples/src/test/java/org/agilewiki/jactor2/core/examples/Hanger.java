package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.messages.SIOp;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

class Hanger extends NonBlockingBladeBase {
    Hanger() throws Exception {
        super(new NonBlockingReactor());
    }

    SIOp<Void> looperSOp() {
        return new SIOp<Void>("looper", getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                while (true) {}
            }
        };
    }

    SOp<Void> sleeperSOp() {
        return new SOp<Void>("sleeper", getReactor()) {
            @Override
            public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                    throw ie;
                }
                return null;
            }
        };
    }
}
