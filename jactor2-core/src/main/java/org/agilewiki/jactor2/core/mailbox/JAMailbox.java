package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.Queue;

/**
 * The extended Mailbox interface for use in the implementation.
 */
public interface JAMailbox extends Mailbox, AutoCloseable, Runnable {

    /**
     * Adds messages directly to the queue.
     *
     * @param messages Previously buffered messages.
     */
    void unbufferedAddMessages(final Queue<Message> messages) throws Exception;
}
