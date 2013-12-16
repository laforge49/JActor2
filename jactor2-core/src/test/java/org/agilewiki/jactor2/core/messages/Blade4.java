package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Test code.
 */
public class Blade4 extends NonBlockingBladeBase {

    public Blade4(final NonBlockingReactor mbox) throws Exception {
        initialize(mbox);
    }

    public <RESPONSE_TYPE> RESPONSE_TYPE local(
            final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return SyncRequest.doLocal(getReactor(), _syncRequest);
    }

    public SyncRequest<Void> hi4SReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                local(new Blade1(getReactor()).hiSReq());
                System.out.println(response);
                return null;
            }
        };
    }
}
