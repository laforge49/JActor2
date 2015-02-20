package org.agilewiki.jactor2.core.impl.messages;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3() throws Exception {
    }

    public SOp<Void> hi3SOp() {
        return new SOp<Void>("hi3", getReactor()) {
            @Override
            protected Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
