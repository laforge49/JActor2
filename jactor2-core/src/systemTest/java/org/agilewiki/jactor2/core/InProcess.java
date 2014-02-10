package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.blades.Delay;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

public class InProcess extends NonBlockingBladeBase {

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            InProcess inProcess = new InProcess();
            inProcess.mightHang().call();
        } finally {
            Plant.close();
        }
    }

    public InProcess() throws Exception {
    }

    public AsyncRequest<Void> mightHang() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) {
                    System.out.println("normal response");
                    if (dis.getPendingResponseCount() == 0)
                        dis.processAsyncResponse(null);
                }
            };

            ExceptionHandler<Void> exceptionHandler = new ExceptionHandler<Void>() {
                @Override
                public void processException(Exception e, AsyncResponseProcessor dat) throws Exception {
                    System.out.println(e);
                    if (dis.getPendingResponseCount() == 0)
                        dis.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                setExceptionHandler(exceptionHandler);
                final IndirectDelay indirectDelay = new IndirectDelay();
                send(indirectDelay.isleep(), responseProcessor);
                send(indirectDelay.isleep(), responseProcessor);
                send(indirectDelay.isleep(), responseProcessor);
                send(indirectDelay.isleep(), responseProcessor);
                send(indirectDelay.isleep(), responseProcessor);
                send(new Delay().sleepSReq(200), new AsyncResponseProcessor<Void>() {
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

    public AsyncRequest<Void> isleep() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                send(new Delay().sleepSReq(10000), this);
            }
        };
    }
}