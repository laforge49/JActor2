package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.MailboxFactory;

public class Firehose {

    private final Engine[] engines;

    public Firehose(final MailboxFactory _mailboxFactory,
                    final Stage... _stages) {
        engines = new Engine[_stages.length];
        int i = 0;
        while (i < _stages.length - 1) {
            engines[i] = new Engine(_mailboxFactory, _stages);
            i += 1;
        }
    }

    public void start() {
        int i = 0;
        while (i < engines.length - 1) {
            engines[i].begin();
            i += 1;
        }
    }

    public void stop() {
        int i = 0;
        while (i < engines.length - 1) {
            engines[i].pause();
            i += 1;
        }
    }
}
