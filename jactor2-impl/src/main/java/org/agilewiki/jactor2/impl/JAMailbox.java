package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;

import java.util.Queue;

/**
 * The extended Mailbox interface for use in the implementation.
 */
public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource, Runnable {

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

    /**
     * Buffer a message to be processed later or add it to the inbox local queue for processing.
     *
     * @param _messageSource The source of the message, or null.
     * @param _message       The message to be processed or the returned results.
     * @param _local         True when the active thread controls the mailbox.
     */
    void addMessage(final MessageSource _messageSource,
                    final Message _message,
                    final boolean _local) throws Exception;
}
