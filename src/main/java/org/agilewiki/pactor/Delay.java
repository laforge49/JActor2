package org.agilewiki.pactor;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class Delay {
    private final Mailbox mailbox;

    public Delay(MailboxFactory mailboxFactory) {
        this.mailbox = mailboxFactory.createMailbox();
    }

    public Request<Void> sleep(final long delay) {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Throwable {
                Thread.sleep(delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
