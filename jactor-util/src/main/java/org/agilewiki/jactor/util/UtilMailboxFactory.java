package org.agilewiki.jactor.util;

import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.impl.JAMailbox;
import org.agilewiki.jactor.impl.MailboxImpl;
import org.agilewiki.jactor.impl.MessageQueue;
import org.agilewiki.jactor.util.firehose.FirehoseMailbox;
import org.slf4j.Logger;

public class UtilMailboxFactory<M extends JAMailbox> extends DefaultMailboxFactoryImpl<M> {

    public final FirehoseMailbox createFirehoseMailbox() {
        return createFirehoseMailbox(initialBufferSize);
    }

    public final FirehoseMailbox createFirehoseMailbox(final int initialBufferSize) {
        return createFirehoseMailbox(messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected FirehoseMailbox createFirehoseMailbox(final MessageQueue messageQueue,
                              final Logger _log, final int _initialBufferSize) {
        return new FirehoseMailbox(this,
                messageQueue, _log, _initialBufferSize);
    }
}
