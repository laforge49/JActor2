package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Delay;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.SAOp;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class InProcess extends NonBlockingBladeBase {

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final InProcess inProcess = new InProcess();
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
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    System.out.println("normal response");
                    if (getAsyncRequestImpl().hasNoPendingResponses())
                        processAsyncResponse(null);
                }
            };

            ExceptionHandler<Void> exceptionHandler = new ExceptionHandler<Void>() {
                @Override
                public void processException(final Exception e,
                        final AsyncResponseProcessor dat) throws Exception {
                    System.out.println(e);
                    if (getAsyncRequestImpl().hasNoPendingResponses()) {
                        processAsyncResponse(null);
                    }
                }
            };

            @Override
            protected void processAsyncOperation(
                    final AsyncRequestImpl _asyncRequestImpl) throws Exception {
                _asyncRequestImpl.setExceptionHandler(exceptionHandler);
                final IndirectDelay indirectDelay = new IndirectDelay();
                _asyncRequestImpl.send(indirectDelay.iSleepOp(),
                        responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(),
                        responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(),
                        responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(),
                        responseProcessor);
                _asyncRequestImpl.send(indirectDelay.iSleepOp(),
                        responseProcessor);
                _asyncRequestImpl.send(new Delay().sleepSOp(200),
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(
                                    final Void _response) {
                                try {
                                    indirectDelay.getReactor().close();
                                } catch (final Exception e) {
                                    throw new IllegalStateException(
                                            "exception on close", e);
                                }
                            }
                        });
            }
        };
    }
}

class IndirectDelay extends NonBlockingBladeBase {
    public IndirectDelay() throws Exception {
    }

    public AOp<Void> iSleepOp() {
        return new AOp<Void>("iSleep", getReactor()) {
            @Override
            protected void processAsyncOperation(
                    final AsyncRequestImpl _asyncRequestImpl,
                    final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(new Delay().sleepSOp(10000),
                        _asyncResponseProcessor);
            }
        };
    }
}