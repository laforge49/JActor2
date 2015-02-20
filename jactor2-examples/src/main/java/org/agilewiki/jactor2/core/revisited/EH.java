package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

import java.io.IOException;

public class EH extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new EH();
        System.out.println("initialized");
    }

    final ExceptionHandler exceptionHandler;

    private EH() throws Exception {

        exceptionHandler = new ExceptionHandler() {
            @Override
            public void processException(Exception e, AsyncResponseProcessor _asyncResponseProcessor) throws Exception {
                Plant.close();
                System.err.println("caught exception:");
                e.printStackTrace();
            }
        };

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

                _asyncRequestImpl.setExceptionHandler(exceptionHandler);

                _asyncRequestImpl.send(new Ex().bad(), runResponseProcessor);
            }
        }.signal();
    }
}

class Ex extends IsolationBladeBase {

    Ex() throws Exception {}

    AReq<Void> bad() {
        return new AReq<Void>("badEx") {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl, AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                throw new IOException();
            }
        };
    }
}
