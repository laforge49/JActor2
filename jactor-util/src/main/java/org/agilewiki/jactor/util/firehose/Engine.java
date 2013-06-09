package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;

public class Engine extends Thread implements Actor, AutoCloseable {

    public final MailboxFactory mailboxFactory;

    public final Stage[] stages;

    public final Mailbox mailbox;

    public Engine(final MailboxFactory _mailboxFactory, final Stage... _stages) {
        super();
        mailboxFactory = _mailboxFactory;
        stages = _stages;
        mailbox = mailboxFactory.createThreadBoundMailbox(new Runnable() {
            @Override
            public void run() {
                interrupt();
            }
        });
        start();
    }

    public void run() {
        int i = 0;
        Object data = null;
        while (true) {
            Stage stage = stages[i];
            while (true) {
                try {
                    stage.acquire();
                } catch (InterruptedException e) {
                    if (mailboxFactory.isClosing())
                        return;
                    mailbox.run();
                    continue;
                }
                break;
            }
            data = stage.process(mailbox, data);
            stage.release();
            i += 1;
            if (i == stages.length) {
                i = 0;
                data = null;
            }
        }
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    @Override
    public void close() throws Exception {
        throw new InterruptedException("closed");
    }
}
