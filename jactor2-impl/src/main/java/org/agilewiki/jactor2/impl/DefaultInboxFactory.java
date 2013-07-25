package org.agilewiki.jactor2.impl;

/**
 * Creates an inbox.
 *
 * @author monster
 */
public class DefaultInboxFactory implements InboxFactory {
    /**
     * Creates a new Inbox instance.
     *
     * @param initialLocalQueueSize The initial size of the local queue.
     */
    @Override
    public Inbox createMessageQueue(final int initialLocalQueueSize) {
        return new DefaultInbox(initialLocalQueueSize);
    }
}
