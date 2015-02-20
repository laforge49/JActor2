package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class AsyncHang extends NonBlockingBladeBase {

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            AsyncHang asyncHang = new AsyncHang();
            asyncHang.hangAOp().call();
        } finally {
            Plant.close();
        }
    }

    public AsyncHang() throws Exception {
    }

    public AOp<Void> hangAOp() {
        return new AOp<Void>("hang", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl, AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                //no response--the request hangs
            }
        };
    }
}
