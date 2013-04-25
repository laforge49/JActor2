package org.agilewiki.pamailbox;

import java.util.Queue;

import org.agilewiki.pactor.Mailbox;

public interface PAMailbox extends Mailbox, AutoCloseable, MessageSource {

    /**
     * A port creates a high-speed one-way connection between two mailboxes.
     * (Ports should generally be opened in pairs.)
     * This method is NOT thread-safe.
     *
     * @param _source The mailbox that originates the request or result message.
     */
    PAMailbox createPort(final Mailbox _source, int size);

    public boolean isFull();

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;
}
