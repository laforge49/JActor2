package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource, Runnable {

    public boolean isFull();

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;

    void addUnbufferedMessage(final Message message, final boolean local)
            throws Exception;

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    boolean isIdler();
}
