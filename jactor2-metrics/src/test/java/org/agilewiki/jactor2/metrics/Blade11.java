package org.agilewiki.jactor2.metrics;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

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
