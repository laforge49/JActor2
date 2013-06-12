package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.Mailbox;

import java.util.Queue;

public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource {

    public boolean isFull();

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;
}
