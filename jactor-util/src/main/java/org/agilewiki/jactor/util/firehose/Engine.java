package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.MailboxFactory;

import java.util.concurrent.Semaphore;

public class Engine extends Thread implements AutoCloseable {

    private final MailboxFactory mailboxFactory;

    private final Stage[] stages;

    private Stage nextStage;

    private Semaphore semaphore = new Semaphore(0);

    private boolean running;

    public boolean isNextStageAvailable() {
        return nextStage.getMailbox().availablePermits() > 0;
    }

    public Engine(
            final MailboxFactory _mailboxFactory,
            final Stage... _stages) {
        super();
        mailboxFactory = _mailboxFactory;
        mailboxFactory.addAutoClosable(this);
        stages = _stages;
        start();
    }

    public void begin() {
        running = true;
        semaphore.release();
    }

    public void pause() {
        running = false;
    }

    public void run() {
        while (true) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                return;
            }
            int i = 0;
            Object data = null;
            Stage stage = stages[0];
            Stage oldStage = null;
            while (true) {
                if (mailboxFactory.isClosing())
                    return;
                while (true) {
                    try {
                        stage.getMailbox().acquire();
                        break;
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                if (oldStage != null)
                    oldStage.getMailbox().release();
                i += 1;
                if (i == stages.length) {
                    i = 0;
                }
                nextStage = stages[i];
                data = stage.process(this, data);
                if (i == 0) {
                    data = null;
                    stage.getMailbox().release();
                    oldStage = null;
                    if (!running)
                        break;
                } else {
                    oldStage = stage;
                }
                stage = nextStage;
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.interrupt();
    }
}
