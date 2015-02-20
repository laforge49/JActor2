package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Order extends IsolationBladeBase {
    Other otherX;
    Other otherY;

    public static void main(final String[] args) throws Exception {
        new Plant();
        new Order();
        System.out.println("initialized");
    }

    private Order() throws Exception {
        new ASig("run") {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                otherX = new Other();
                otherY = new Other();
                final AsyncResponseProcessor<Void> runResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        Plant.close();
                        System.out.println("finished");
                    }
                };
                final AsyncResponseProcessor<Void> runResponseProcessor3 = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        _asyncRequestImpl.send(otherY.run(otherX, "Y -> X"), runResponseProcessor);
                    }
                };
                final AsyncResponseProcessor<Void> runResponseProcessor2 = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        _asyncRequestImpl.send(otherY.run(otherY, "Y -> Y"), runResponseProcessor3);
                    }
                };
                final AsyncResponseProcessor<Void> runResponseProcessor1 = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        _asyncRequestImpl.send(otherX.run(otherY, "X -> Y"), runResponseProcessor2);
                    }
                };
                _asyncRequestImpl.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Exception e, AsyncResponseProcessor _asyncResponseProcessor) throws Exception {
                        Plant.close();
                        System.err.println("caught exception:");
                        e.printStackTrace();
                    }
                });
                _asyncRequestImpl.send(otherX.run(otherX, "X -> X"), runResponseProcessor1);
            }
        }.signal();
    }
}

class Other extends IsolationBladeBase {
    Other() throws Exception {}

    AReq<Void> run(final Other _other, final String _i) {
        return new AReq<Void>("runOther") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(_other.blip(_i), _asyncResponseProcessor);
            }
        };
    }

    AReq<Void> blip(final String _i) {
        return new AReq<Void>("blip") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor _asyncResponseProcessor)
                    throws Exception {
                System.err.println("blip " + _i);
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
