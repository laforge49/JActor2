package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.MailboxFactory;

public class Firehose {

    public Firehose(final MailboxFactory _mailboxFactory,
                    final Stage[] _stages) {
        int i = 0;
        while (i < _stages.length) {
            new Engine(_mailboxFactory, _stages);
            i += 1;
        }
    }
}
