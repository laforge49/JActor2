package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

//Exploring the use of multiple contexts.
public class ServiceSample {

    public static void main(final String[] _args) throws Exception {

        //Application context with 1 thread.
        final ModuleContext applicationContext = new ModuleContext(1);

        //Create a service actor that uses its own context.
        Service service = new Service();

        try {
            //Test the delay echo request on the service actor.
            System.out.println(service.delayEchoReq(1, "1 (Expected)").call());

            //close the context used by the service actor.
            service.getMessageProcessor().getModuleContext().close();
            try {
                //Try using delay echo request with the context closed.
                System.out.println(service.delayEchoReq(1, "(Unexpected)").call());
            } catch (ServiceClosedException sce) {
                //The ServiceClosedException is now thrown because the context is closed.
                System.out.println("Exception as expected");
            }

            //Create a new service actor that uses its own context.
            service = new Service();
            //Create an application actor based on the application context
            //and with a reference to the service actor.
            final ServiceApplication serviceApplication =
                    new ServiceApplication(service, new NonBlockingMessageProcessor(applicationContext));
            //Start a delay echo service request using the application actor.
            EchoReqState echoReqState = serviceApplication.echoReq(1, "2 (Expected)").call();
            //Print the results of the delay echo service request.
            System.out.println(serviceApplication.echoResultReq(echoReqState).call());

            //Start a second delay echo service request using the application actor.
            EchoReqState echoReqState2 = serviceApplication.echoReq(1, "(Unexpected)").call();
            //Close the service context while the delay echo service request is still sleeping.
            serviceApplication.closeServiceReq().call();
            //The results should now show that an exception was thrown.
            System.out.println(serviceApplication.echoResultReq(echoReqState2).call());
        } finally {
            service.getMessageProcessor().getModuleContext().close(); //Close the service context.
            applicationContext.close(); //Close the application context.
        }

    }
}

//A service actor that runs on its own context.
class Service extends ActorBase {

    Service() throws Exception {
        //Create a processing on a new context with 1 thread.
        initialize(new NonBlockingMessageProcessor(new ModuleContext(1)));
    }

    //Returns a delay echo request.
    Request<String> delayEchoReq(final int _delay, final String _text) {
        return new Request<String>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                //Sleep a bit so that the request does not complete too quickly.
                try {
                    Thread.sleep(_delay);
                } catch (InterruptedException e) {
                    return;
                }
                //Echo the text back in the response.
                _transport.processResponse("Echo: " + _text);
            }
        };
    }

}

//Holds the state of a service application echo request.
class EchoReqState {
    //Not null when an echoResultRequest was received before
    // the result of the matching service delay echo request.
    Transport<String> transport;

    //Not null when the result of the service delay echo request is received
    //before the matching echoResultRequest.
    String response;
}

//An actor with a context that is different than the context of the service actor.
class ServiceApplication extends ActorBase {

    //The service actor, which operates in a different context.
    private final Service service;

    //Create a service application actor with a reference to a service actor.
    ServiceApplication(final Service _service, final MessageProcessor _messageProcessor) throws Exception {
        service = _service;
        initialize(_messageProcessor);
    }

    //Returns an application echo request.
    //The echo request is used to initiate a service delay echo request.
    //And the response returned by the echo request is state data needed to manage the
    //delivery of the response from the service delay echo request.
    Request<EchoReqState> echoReq(final int _delay, final String _text) {
        return new Request<EchoReqState>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<EchoReqState> _transport) throws Exception {

                //State data needed to manage the delivery of the response from
                //the service delay echo request.
                final EchoReqState echoReqState = new EchoReqState();

                //Establish an exception handler which traps a ServiceClosedException and
                //returns a notification that the exception occurred as a result.
                getMessageProcessor().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (throwable instanceof ServiceClosedException) {
                            String response = "Exception as expected";
                            if (echoReqState.transport == null) {
                                //No echo result request has yet been received,
                                //so save the response for later.
                                echoReqState.response = response;
                            } else {
                                //An echo result request has already been received,
                                //so now is the time to return the response.
                                echoReqState.transport.processResponse(response);
                            }
                        } else
                            throw throwable;
                    }
                });
                service.delayEchoReq(_delay, _text).send(getMessageProcessor(), new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(String response) throws Exception {
                        if (echoReqState.transport == null) {
                            //No echo result request has yet been received,
                            //so save the response for later.
                            echoReqState.response = response;
                        } else {
                            //An echo result request has already been received,
                            //so now is the time to return the response.
                            echoReqState.transport.processResponse(response);
                        }
                    }
                });
                _transport.processResponse(echoReqState);
            }
        };
    }

    //Returns a close service request.
    Request<Void> closeServiceReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                //Close the context of the service actor.
                service.getMessageProcessor().getModuleContext().close();
                _transport.processResponse(null);
            }
        };
    }

    //Returns an echo result request.
    //An echo result request returns the response from the service delay echo request
    //associated with the given echo request state.
    Request<String> echoResultReq(final EchoReqState _echoReqState) {
        return new Request<String>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                if (_echoReqState.response == null) {
                    //There is as yet no response from the associated service delay echo request,
                    //so save the transport of this request for subsequent delivery of that belated response.
                    _echoReqState.transport = _transport;
                } else {
                    //The response from the associated service delay echo request is already present,
                    //so return that response now.
                    _transport.processResponse(_echoReqState.response);
                }
            }
        };
    }
}