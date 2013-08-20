package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.context.JAContext;

import javax.swing.*;

/**
 * Messages are processed on Swing's event-dispatch thread when an actor uses
 * a SwingBoundMessageProcessor. This is critical, as so many Swing methods are
 * not thread-safe.
 */
public class SwingBoundMessageProcessor extends ThreadBoundMessageProcessor {

    /**
     * Create a mailbox processor bound to the Swing event-dispatch thread.
     *
     * @param _jaContext The context of the mailbox processor.
     */
    public SwingBoundMessageProcessor(JAContext _jaContext) {
        super(_jaContext, null);

    }

    /**
     * Create a mailbox processor bound to the Swing event-dispatch thread.
     *
     * @param _jaContext             The context of the mailbox processor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public SwingBoundMessageProcessor(JAContext _jaContext, int _initialOutboxSize, int _initialLocalQueueSize) {
        super(_jaContext, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    protected void afterAdd() {
        SwingUtilities.invokeLater(this);
    }
}
