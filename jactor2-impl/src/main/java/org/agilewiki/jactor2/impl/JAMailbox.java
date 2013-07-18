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
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();

    /**
     * Returns true, if the message was buffered for sending later.
     *
     * @param message Message to send-buffer
     * @param target  The mailbox that should eventually receive this message
     * @return true, if buffered
     */
    boolean buffer(final Message message, final Mailbox target);
}
