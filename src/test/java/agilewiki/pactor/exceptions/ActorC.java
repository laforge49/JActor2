package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorC {
    private final Mailbox mailbox;

    public ActorC(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<String> throwRequest() {
        return new Request<String>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<String> responseProcessor) throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        responseProcessor.processResponse(throwable.toString());
                    }
                });
                throw new SecurityException("thrown on request");
            }
        };
    }
}
