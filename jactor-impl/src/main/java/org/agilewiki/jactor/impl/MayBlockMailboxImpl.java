package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.MayBlockMailbox;
import org.slf4j.Logger;

public class MayBlockMailboxImpl extends BaseMailbox implements MayBlockMailbox {
    public MayBlockMailboxImpl(Runnable _onIdle, JAMailboxFactory factory, MessageQueue messageQueue, Logger _log, int _initialBufferSize) {
        super(true, _onIdle, factory, messageQueue, _log, _initialBufferSize);
    }
}
