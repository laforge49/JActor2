package org.agilewiki.pactor.util;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        this.mailbox = mailboxFactory.createMailbox();
    }

    public RequestBase<Void> sleep(final long delay) {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
