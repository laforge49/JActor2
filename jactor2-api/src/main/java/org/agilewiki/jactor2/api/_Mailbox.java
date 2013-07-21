package org.agilewiki.jactor2.api;

import org.slf4j.Logger;

/**
 * _Mailbox defines the internal API used by RequestBase and Event
 * to pass _Request's to a target mailbox.
 */
interface _Mailbox extends MessageSource {

    /**
     * Add a message directly to the queue.
     *
     * @param message A message.
     * @param local   True when the current thread is bound to the mailbox.
     */
    void unbufferedAddMessages(final Message message, final boolean local)
            throws Exception;

    /**
     * Returns true, if the message was buffered for sending later.
     *
     * @param message Message to send-buffer
     * @param target  The mailbox that should eventually receive this message
     * @return true, if buffered
     */
    boolean buffer(final Message message, final Mailbox target);

    /**
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();

    Message getCurrentMessage();

    ExceptionHandler getExceptionHandler();

    Logger getLogger();

    void setCurrentMessage(Message message);
}
