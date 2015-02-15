package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * Test code.
 */
public class Blade11 extends IsolationBladeBase {

    public Blade11(final IsolationReactor mbox) throws Exception {
        super(mbox);
    }

    public SReq<String> hiSReq() {
        return new SReq<String>("hi") {
            @Override
            protected String processSyncOperation(RequestImpl _requestImpl) throws Exception {
                return "Hello world!";
            }
        };
    }

    public AReq<String> hoAReq() {
        return new AReq<String>("ho") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<String> _asyncResponseProcessor)
                    throws Exception {
                _asyncResponseProcessor.processAsyncResponse("hee");
            }
        };
    }

    public ASig humASig() {
        return new ASig("hum") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
