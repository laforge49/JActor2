package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.*;

public class ActorC {
    private final Mailbox mailbox;

    public ActorC(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<String> throwRequest() {
        return new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<String> responseProcessor)
                    throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        responseProcessor.processResponse(throwable.toString());
                    }
                });
                throw new SecurityException("thrown on request");
            }
        };
    }
}
