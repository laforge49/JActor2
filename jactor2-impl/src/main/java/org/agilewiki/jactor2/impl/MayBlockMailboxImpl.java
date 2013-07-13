package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.MayBlockMailbox;
import org.slf4j.Logger;

public class MayBlockMailboxImpl extends UnboundMailboxImpl implements MayBlockMailbox {

    /**
     * Create a mailbox.
     *
     * @param _onIdle            Object to be run when the inbox is emptied, or null.
     * @param _factory           The factory of this object.
     * @param _inbox      The inbox.
     * @param _log               The Mailbox log.
     * @param _initialBufferSize Initial size of the outbox for each unique message destination.
     */
    public MayBlockMailboxImpl(Runnable _onIdle,
                               JAMailboxFactory _factory,
                               Inbox _inbox,
                               Logger _log,
                               int _initialBufferSize) {
        super(_onIdle, _factory, _inbox, _log, _initialBufferSize);
    }

    @Override
    protected void afterProcessMessage(final boolean request,
                                       final Message message) {
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (Exception e) {
            log.error("Exception thrown by onIdle", e);
        }
    }
}
