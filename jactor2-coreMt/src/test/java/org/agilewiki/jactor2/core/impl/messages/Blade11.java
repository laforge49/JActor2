package org.agilewiki.jactor2.core.impl.messages;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Test code.
 */
public class Blade11 extends IsolationBladeBase {

    public Blade11(final IsolationReactor mbox) throws Exception {
        super(mbox);
    }

    public SOp<String> hiSOp() {
        return new SOp<String>("hi", getReactor()) {
            @Override
            protected String processSyncOperation(RequestImpl _requestImpl) throws Exception {
                return "Hello world!";
            }
        };
    }
}
