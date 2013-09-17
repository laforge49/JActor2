package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A facility with two threads.
        final Facility facility = new Facility(2);

        try {

            //Create an ExceptionBlade.
            ExceptionBlade exceptionBlade = new ExceptionBlade(new NonBlockingReactor(facility));

            try {
                //Create and call an exception request.
                exceptionBlade.exceptionAReq().call();
                System.out.println("can not get here");
            } catch (IllegalStateException ise) {
                System.out.println("got first IllegalStateException, as expected");
            }

            //Create an ExceptionHandlerBlade.
            ExceptionHandlerBlade exceptionHandlerBlade =
                    new ExceptionHandlerBlade(exceptionBlade, new NonBlockingReactor(facility));
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerBlade.testAReq().call());

        } finally {
            //shutdown the facility
            facility.close();
        }
    }
}

//A blade with a request that throws an exception.
class ExceptionBlade extends BladeBase {

    //Create an ExceptionBlade.
    ExceptionBlade(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Returns an exception request.
    AsyncRequest<Void> exceptionAReq() {
        return new AsyncRequest<Void>(getReactor()) {
            @Override
            protected void processAsyncRequest() throws Exception {
                throw new IllegalStateException(); //Throw an exception when the request is processed.
            }
        };
    }
}

//A blade with an exception handler.
class ExceptionHandlerBlade extends BladeBase {

    //A blade with a request that throws an exception.
    private final ExceptionBlade exceptionBlade;

    //Create an exception handler blade with a reference to an exception blade.
    ExceptionHandlerBlade(final ExceptionBlade _exceptionBlade, final Reactor _reactor) throws Exception {
        exceptionBlade = _exceptionBlade;
        initialize(_reactor);
    }

    //Returns a test request.
    AsyncRequest<String> testAReq() {
        return new AsyncRequest<String>(getReactor()) {
            AsyncRequest<String> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {

                //Create and assign an exception handler.
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception _exception) throws Exception {
                        if (_exception instanceof IllegalStateException) {
                            //Returns a result if an IllegalStateException was thrown.
                            return "got IllegalStateException, as expected";
                        } else //Otherwise rethrow the exception.
                            throw _exception;
                    }
                });

                //Create an exception request and send it to the exception blade for processing.
                //The thrown exception is then caught by the assigned exception handler.
                exceptionBlade.exceptionAReq().send(getMessageProcessor(), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) throws Exception {
                        dis.processAsyncResponse("can not get here");
                    }
                });
            }
        };
    }

}