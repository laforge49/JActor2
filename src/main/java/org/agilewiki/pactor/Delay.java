package org.agilewiki.pactor;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        this.mailbox = mailboxFactory.createMailbox();
    }

    public Request<Void> sleep(final long delay) {
        return new Request<Void>(mailbox) {
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
