package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Mailbox;

public class Load extends StageBase {
    int delay;

    public Load(final int _delay) {
        delay = _delay;
    }

    @Override
    public Object process(Engine _engine, Object data) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            try {
                Mailbox mailbox = _engine.mailbox;
                if (!mailbox.getMailboxFactory().isClosing())
                    mailbox.run();
                else
                    _engine.close();
            } catch (Exception e) {
            }
        }
        return data;
    }
}
