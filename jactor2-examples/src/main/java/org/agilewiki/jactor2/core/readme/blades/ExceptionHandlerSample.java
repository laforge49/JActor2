package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.io.IOException;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create an ExceptionBlade.
            ExceptionBlade exceptionBlade = new ExceptionBlade(new NonBlockingReactor());

            try {
                //Create and call an exception request.
                exceptionBlade.exceptionAOp().call();
                System.out.println("can not get here");
            } catch (IOException ise) {
                System.out.println("got first IOException, as expected");
            }

            //Create an ExceptionHandlerBlade.
            ExceptionHandlerBlade exceptionHandlerBlade =
                    new ExceptionHandlerBlade(exceptionBlade, new NonBlockingReactor());
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerBlade.testAOp().call());

        } finally {
            //shutdown the facility
            Plant.close();
        }
    }
}

//A blades with a request that throws an exception.
class ExceptionBlade extends NonBlockingBladeBase {

    //Create an ExceptionBlade.
    ExceptionBlade(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    //Returns an exception request.
    AOp<Void> exceptionAOp() {
        return new AOp<Void>("exception", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                throw new IOException(); //Throw an exception when the request is processed.
            }
        };
    }
}

//A blades with an exception handler.
class ExceptionHandlerBlade extends NonBlockingBladeBase {

    //A blades with a request that throws an exception.
    private final ExceptionBlade exceptionBlade;

    //Create an exception handler blades with a reference to an exception blades.
    ExceptionHandlerBlade(final ExceptionBlade _exceptionBlade, final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
        exceptionBlade = _exceptionBlade;
    }

    //Returns a test request.
    AOp<String> testAOp() {
        return new AOp<String>("test", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<String> _asyncResponseProcessor)
                    throws Exception {
                //Create and assign an exception handler.
                _asyncRequestImpl.setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception _exception) throws Exception {
                        if (_exception instanceof IOException) {
                            //Returns a result if an IllegalStateException was thrown.
                            return "got IOException, as expected";
                        } else { //Otherwise rethrow the exception.
                            throw _exception;
                        }
                    }
                });

                //Create an exception request and doSend it to the exception blades for processing.
                //The thrown exception is then caught by the assigned exception handler.
                _asyncRequestImpl.send(exceptionBlade.exceptionAOp(), _asyncResponseProcessor, "can not get here");
            }
        };
    }

}