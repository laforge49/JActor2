package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.impl.MailboxImpl;
import org.agilewiki.jactor.impl.MessageQueue;
import org.agilewiki.jactor.util.UtilMailboxFactory;
import org.slf4j.Logger;

public class FirehoseMailbox extends MailboxImpl {

    public FirehoseMailbox(UtilMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(true, null, null, factory, messageQueue, _log, _initialBufferSize);
    }
}
