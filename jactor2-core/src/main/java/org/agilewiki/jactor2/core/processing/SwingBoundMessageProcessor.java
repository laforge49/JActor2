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
     * Create a message processor processor bound to the Swing event-dispatch thread.
     *
     * @param _jaContext The context of the message processor.
     */
    public SwingBoundMessageProcessor(JAContext _jaContext) {
        super(_jaContext, null);

    }

    /**
     * Create a message processor bound to the Swing event-dispatch thread.
     *
     * @param _jaContext             The context of the message processor.
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
