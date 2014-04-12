package org.agilewiki.jactor2.core.mt.blades;

import org.agilewiki.jactor2.core.Plant;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create an ExceptionBlade.
            ExceptionBlade exceptionBlade = new ExceptionBlade(new NonBlockingReactor());

            try {
                //Create and call an exception request.
                exceptionBlade.exceptionAReq().call();
                System.out.println("can not get here");
            } catch (IllegalStateException ise) {
                System.out.println("got first IllegalStateException, as expected");
            }

            //Create an ExceptionHandlerBlade.
            ExceptionHandlerBlade exceptionHandlerBlade =
                    new ExceptionHandlerBlade(exceptionBlade, new NonBlockingReactor());
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerBlade.testAReq().call());

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
    AsyncRequest<Void> exceptionAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                throw new IllegalStateException(); //Throw an exception when the request is processed.
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
    AsyncRequest<String> testAReq() {
        return new AsyncBladeRequest<String>() {
            AsyncRequest<String> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {

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

                //Create an exception request and doSend it to the exception blades for processing.
                //The thrown exception is then caught by the assigned exception handler.
                send(exceptionBlade.exceptionAReq(), dis, "can not get here");
            }
        };
    }

}