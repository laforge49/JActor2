package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Message;

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

    /**
     * Add a message directly to the queue.
     *
     * @param message A message.
     * @param local   True when the current thread is bound to the mailbox.
     */
    void unbufferedAddMessages(final Message message, final boolean local)
            throws Exception;

    /**
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();
}
