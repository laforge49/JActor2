package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.NonBlockingMailbox;
import org.slf4j.Logger;

public class NonBlockingMailboxImpl extends BaseMailbox implements NonBlockingMailbox {
    public NonBlockingMailboxImpl(Runnable _onIdle, JAMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(false, _onIdle, factory, messageQueue, _log, _initialBufferSize);
    }
}
