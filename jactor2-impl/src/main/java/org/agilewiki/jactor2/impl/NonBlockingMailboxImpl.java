package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.NonBlockingMailbox;
import org.slf4j.Logger;

public class NonBlockingMailboxImpl extends UnboundMailboxImpl implements NonBlockingMailbox {

    /**
     * Create a mailbox.
     *
     * @param _onIdle            Object to be run when the inbox is emptied, or null.
     * @param _factory            The factory of this object.
     * @param _messageQueue       The inbox.
     * @param _log               The Mailbox log.
     * @param _initialBufferSize Initial size of the outbox for each unique message destination.
     */
    public NonBlockingMailboxImpl(Runnable _onIdle,
                                  JAMailboxFactory _factory,
                                  MessageQueue _messageQueue, Logger _log, int _initialBufferSize) {
        super(_onIdle, _factory, _messageQueue, _log, _initialBufferSize);
    }
}
