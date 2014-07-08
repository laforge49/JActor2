package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.ThreadBoundBladeBase;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * Test code.
 */
public class Blade11 extends ThreadBoundBladeBase {

    public Blade11(final ThreadBoundReactor mbox) throws Exception {
        super(mbox);
    }

    public SOp<String> hiSOp() {
        return new SOp<String>("hi", getReactor()) {
            @Override
            public String processSyncOperation(RequestImpl _requestImpl) throws Exception {
                return "Hello world!";
            }
        };
    }
}
