package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorD {
    private final Mailbox mailbox;

    public ActorD(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    Request<Void> doSomethin() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Throwable {
                responseProcessor.processResponse(null);
            }
        };
    }

    public Request<String> throwRequest() {
        return new Request<String>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<String> responseProcessor) throws Throwable {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        responseProcessor.processResponse(throwable.toString());
                    }
                });
                doSomethin().reply(mailbox, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Throwable {
                        throw new SecurityException("thrown on request");
                    }
                });
            }
        };
    }
}
