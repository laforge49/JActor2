package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;

public class ServiceSample {

    public static void main(final String[] _args) throws Exception {

        JAContext serviceContext = new JAContext(1);
        final JAContext applicationContext = new JAContext(1);
        try {
            Service service = new Service(new NonBlockingMailbox(serviceContext));
            System.out.println(service.delayEchoReq(1, "1 (Expected)").call());
            serviceContext.close();
            try {
                System.out.println(service.delayEchoReq(1, "(Unexpected)").call());
            } catch (ServiceClosedException sce) {
                System.out.println("Exception as expected");
            }
            serviceContext = new JAContext(1);
            service = new Service(new NonBlockingMailbox(serviceContext));
            final ServiceApplication serviceApplication =
                    new ServiceApplication(service, new NonBlockingMailbox(applicationContext));
            EchoReqState echoReqState = serviceApplication.echoReq(1, "2 (Expected)").call();
            System.out.println(serviceApplication.echoResultReq(echoReqState).call());
            EchoReqState echoReqState2 = serviceApplication.echoReq(1, "(Unexpected)").call();
            serviceApplication.closeServiceReq().call();
            System.out.println(serviceApplication.echoResultReq(echoReqState2).call());
        } finally {
            serviceContext.close();
            applicationContext.close();
        }

    }
}

class Service extends ActorBase {

    Service(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    Request<String> delayEchoReq(final int _delay, final String _text) {
        return new Request<String>(getMailbox()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                try {
                    Thread.sleep(_delay);
                } catch (InterruptedException e) {
                    return;
                }
                _transport.processResponse("Echo: " + _text);
            }
        };
    }

}

class EchoReqState {
    Transport<String> transport;
    String response;
}

class ServiceApplication extends ActorBase {
    private final Service service;

    ServiceApplication(final Service _service, final Mailbox _mailbox) throws Exception {
        service = _service;
        initialize(_mailbox);
    }
    Request<EchoReqState> echoReq(final int _delay, final String _text) {
        return new Request<EchoReqState>(getMailbox()) {
            @Override
            public void processRequest(Transport<EchoReqState> _transport) throws Exception {
                final EchoReqState echoReqState = new EchoReqState();
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (throwable instanceof ServiceClosedException) {
                            String response = "Exception as expected";
                            if (echoReqState.transport == null)
                                echoReqState.response = response;
                            else
                                echoReqState.transport.processResponse(response);
                        } else
                            throw throwable;
                    }
                });
                service.delayEchoReq(_delay, _text).send(getMailbox(), new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(String response) throws Exception {
                        if (echoReqState.transport == null)
                            echoReqState.response = response;
                        else
                            echoReqState.transport.processResponse(response);
                    }
                });
                _transport.processResponse(echoReqState);
            }
        };
    }

    Request<Void> closeServiceReq() {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                service.getMailbox().getJAContext().close();
                _transport.processResponse(null);
            }
        };
    }

    Request<String> echoResultReq(final EchoReqState _echoReqState) {
        return new Request<String>(getMailbox()) {
            @Override
            public void processRequest(Transport<String> _transport) throws Exception {
                if (_echoReqState.response == null)
                    _echoReqState.transport = _transport;
                else
                    _transport.processResponse(_echoReqState.response);
            }
        };
    }
}