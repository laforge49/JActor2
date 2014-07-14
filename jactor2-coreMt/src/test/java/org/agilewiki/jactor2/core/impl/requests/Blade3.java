package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3() throws Exception {
    }

    public SOp<Void> hi3SOp() {
        return new SOp<Void>("hi3", getReactor()) {
            @Override
            public Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
