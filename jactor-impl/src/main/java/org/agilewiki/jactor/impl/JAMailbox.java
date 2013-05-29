package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.Mailbox;

import java.util.Queue;

public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource {

    /**
     * A port creates a high-speed one-way connection between two mailboxes.
     * (Ports should generally be opened in pairs.)
     * This method is NOT thread-safe.
     *
     * @param _source The mailbox that originates the request or result message.
     */
    JAMailbox createPort(final Mailbox _source, int size);

    public boolean isFull();

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;
}
