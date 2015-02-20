package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Signal extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new Signal();
        System.out.println("initialized");
    }

    private Signal() throws Exception {
        new ASig("run") {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> runResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        Plant.close();
                        System.out.println("finished");
                    }
                };
                _asyncRequestImpl.send(new Ping(Signal.this).ping(), runResponseProcessor);
            }
        }.signal();
    }

    void blip() {
        new ASig("blip") {

            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                System.out.println("blip");
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        }.signal();
    }
}

class Ping extends IsolationBladeBase {
    private final Signal signal;
    Ping(final Signal _signal) throws Exception {
        signal = _signal;
    }

    AReq<Void> ping() {
        return new AReq<Void>("runPing") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                signal.blip();
                _asyncResponseProcessor.processAsyncResponse(null);
            };
        };
    }
}
