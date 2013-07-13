package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource, Runnable {

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;

    void addUnbufferedMessage(final Message message, final boolean local)
            throws Exception;
}
