import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

import java.io.IOException;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create an ExceptionBlade.
            ExceptionBlade exceptionBlade = new ExceptionBlade();

            try {
                //Create and call an exception request.
                exceptionBlade.exceptionAReq().call();
                System.out.println("can not get here");
            } catch (IOException ise) {
                System.out.println("got first IOException, as expected");
            }

            //Create an ExceptionHandlerBlade.
            ExceptionHandlerBlade exceptionHandlerBlade =
                    new ExceptionHandlerBlade(exceptionBlade);
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerBlade.testAReq().call());

        } finally {
            //shutdown the facility
            Plant.close();
        }
    }
}

//A blade with a request that throws an exception.
class ExceptionBlade extends NonBlockingBladeBase {
	ExceptionBlade() throws Exception {
	}

    //Returns an exception request.
    AsyncRequest<Void> exceptionAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws IOException {
                throw new IOException(); //Throw an exception when the request is processed.
            }
        };
    }
}

//A blade with an exception handler.
class ExceptionHandlerBlade extends NonBlockingBladeBase {

    //A blade with a request that throws an exception.
    private final ExceptionBlade exceptionBlade;

    //Create an exception handler blade with a reference to an exception blade.
    ExceptionHandlerBlade(final ExceptionBlade _exceptionBlade) throws Exception {
        exceptionBlade = _exceptionBlade;
    }

    //Returns a test request.
    AsyncRequest<String> testAReq() {
        return new AsyncBladeRequest<String>() {
            AsyncRequest<String> dis = this;

            @Override
            public void processAsyncRequest() {

                //Create and assign an exception handler.
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception _exception) throws Exception {
                        if (_exception instanceof IOException) {
                            //Returns a result if an IOException was thrown.
                            return "got IOException, as expected";
                        } else //Otherwise rethrow the exception.
                            throw _exception;
                    }
                });

                //Create an exception request and doSend it to the exception blade for processing.
                //The thrown exception is then caught by the assigned exception handler.
                send(exceptionBlade.exceptionAReq(), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) {
                        dis.processAsyncResponse("can not get here");
                    }
                });
            }
        };
    }

}