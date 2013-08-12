package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A context with two threads.
        final JAContext jaContext = new JAContext(2);

        try {

            //Create an ExceptionActor.
            ExceptionActor exceptionActor = new ExceptionActor(new NonBlockingMailbox(jaContext));

            try {
                //Create and call an exception request.
                exceptionActor.exceptionReq().call();
                System.out.println("can not get here");
            } catch (IllegalStateException ise) {
                System.out.println("got first IllegalStateException, as expected");
            }

            //Create an ExceptionHandlerActor.
            ExceptionHandlerActor exceptionHandlerActor =
                    new ExceptionHandlerActor(exceptionActor, new NonBlockingMailbox(jaContext));
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerActor.testReq().call());

        } finally {
            //shutdown the context
            jaContext.close();
        }
    }
}

//An actor with a request that throws an exception.
class ExceptionActor extends ActorBase {

    //Create an ExceptionActor.
    ExceptionActor(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    //Returns an exception request.
    Request<Void> exceptionReq() {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                throw new IllegalStateException(); //Throw an exception when the request is processed.
            }
        };
    }
}

//An actor with an exception handler.
class ExceptionHandlerActor extends ActorBase {

    //An actor with a request that throws an exception.
    private final ExceptionActor exceptionActor;

    //Create an exception handler actor with a reference to an exception actor.
    ExceptionHandlerActor(final ExceptionActor _exceptionActor, final Mailbox _mailbox) throws Exception {
        exceptionActor = _exceptionActor;
        initialize(_mailbox);
    }

    //Returns a test request.
    Request<String> testReq() {
        return new Request<String>(getMailbox()) {

            @Override
            public void processRequest(final Transport<String> _transport) throws Exception {

                //Create and assign an exception handler.
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (throwable instanceof IllegalStateException) {
                            //Returns a result if an IllegalStateException was thrown.
                            _transport.processResponse("got IllegalStateException, as expected");
                        } else //Otherwise rethrow the exception.
                            throw throwable;
                    }
                });

                //Create an exception request and send it to the exception actor for processing.
                //The thrown exception is then caught by the assigned exception handler.
                exceptionActor.exceptionReq().send(getMailbox(), new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Exception {
                        _transport.processResponse("can not get here");
                    }
                });
            }
        };
    }

}