package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Timeout extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new Timeout();
        System.out.println("initialized");
    }

    private Timeout() throws Exception {
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
                _asyncRequestImpl.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Exception e, AsyncResponseProcessor _asyncResponseProcessor) throws Exception {
                        Plant.close();
                        System.err.println("caught exception:");
                        e.printStackTrace();
                    }
                });
                _asyncRequestImpl.send(new Worker(0).run(10000000000L, -1), runResponseProcessor);
            }
        }.signal();
    }
}
