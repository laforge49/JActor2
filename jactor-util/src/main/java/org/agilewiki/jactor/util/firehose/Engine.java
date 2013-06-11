package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;

public class Engine extends Thread implements Actor, AutoCloseable {

    public final MailboxFactory mailboxFactory;

    public final Stage[] stages;

    public final Mailbox mailbox;

    private Stage nextStage;

    public boolean isNextStageAvailable() {
        return nextStage.availablePermits() > 0;
    }

    public Engine(final MailboxFactory _mailboxFactory, final Stage... _stages) {
        super();
        mailboxFactory = _mailboxFactory;
        mailboxFactory.addAutoClosable(this);
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
        Stage stage = stages[0];
        Stage oldStage = null;
        while (true) {
            if (mailboxFactory.isClosing())
                return;
            while (true) {
                try {
                    stage.acquire();
                    break;
                } catch (InterruptedException e) {
                    if (mailboxFactory.isClosing())
                        return;
                    mailbox.run();
                    continue;
                }
            }
            if (oldStage != null)
                oldStage.release();
            i += 1;
            if (i == stages.length) {
                i = 0;
            }
            nextStage = stages[i];
            data = stage.process(this, data);
            if (i == 0) {
                data = null;
                stage.release();
                oldStage = null;
            } else {
                oldStage = stage;
            }
            stage = nextStage;
        }
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    @Override
    public void close() throws Exception {
        this.interrupt();
    }
}
