package org.agilewiki.jactor2.api;

import org.slf4j.Logger;

/**
 * _Mailbox defines an internal API of Mailbox used by Request and Event.
 */
interface _Mailbox extends MessageSource {

    /**
     * Add a message directly to the input queue of a Mailbox.
     *
     * @param message A message.
     * @param local   True when the current thread is bound to the mailbox.
     */
    void unbufferedAddMessages(final Message message, final boolean local)
            throws Exception;

    /**
     * Buffers a message in the sending mailbox for sending later.
     *
     * @param message Message to be buffered.
     * @param target  The mailbox that should eventually receive this message
     * @return True if the message was buffered.
     */
    boolean buffer(final Message message, final Mailbox target);

    /**
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();

    /**
     * Returns the message currently being processed.
     * @return The message currently being processed.
     */
    Message getCurrentMessage();

    /**
     * The current exception handler.
     *
     * @return The current exception handler, or null.
     */
    ExceptionHandler getExceptionHandler();

    /**
     * Returns the mailbox logger.
     * @return The mailbox logger.
     */
    Logger getLogger();

    /**
     * Identify the message currently being processed.
     * @param message The message currently being processed.
     */
    void setCurrentMessage(Message message);
}
