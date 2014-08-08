package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Delay;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.*;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class InProcess extends NonBlockingBladeBase {

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            InProcess inProcess = new InProcess();
            inProcess.mightHangOp().call();
        } finally {
            Plant.close();
        }
    }

    public InProcess() throws Exception {
    }

    public AOp<Void> mightHangOp() {
        return new SAOp<Void>("mightHang", getReactor()) {
            AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    System.out.println("normal response");
                    if (getAsyncRequestImpl().getPendingResponseCount() == 0)
                        processAsyncResponse(null);
                }
            };

            ExceptionHandler<Void> exceptionHandler = new ExceptionHandler<Void>() {
                @Override
                public void processException(Exception e, AsyncResponseProcessor dat)
                        throws Exception {
                    System.out.println(e);
                    if (getAsyncRequestImpl().getPendingResponseCount() == 0) {
                        processAsyncResponse(null);
                    }
                }
            };

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl)
                    throws Exception {
                _asyncRequestImpl.setExceptionHandler(exceptionHandler);
                final IndirectDelay indirectDelay = new IndirectDelay();
                _asyncRequestImpl.send(indirectDelay.iSleepOp(), responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(), responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(), responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(), responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(), responseProcessor);
                _asyncRequestImpl.send(new Delay().sleepSOp(200), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) {
                        try {
                            indirectDelay.getReactor().close();
                        } catch (Exception e) {
                            throw new IllegalStateException("exception on close", e);
                        }
                    }
                });
            }
        };
    }
}

class IndirectDelay extends NonBlockingBladeBase {
    public IndirectDelay() throws Exception {}

    public AOp<Void> iSleepOp() {
        return new AOp<Void>("iSleep", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(new Delay().sleepSOp(10000), _asyncResponseProcessor);
            }
        };
    }
}