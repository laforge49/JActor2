package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.Mailbox;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource, Runnable {

    public boolean isFull();

    /**
     * Adds messages to the queue.
     */
    void addUnbufferedMessages(final Queue<Message> messages) throws Exception;

    /**
     * Returns the atomic reference to the current thread.
     *
     * @return
     */
    AtomicReference<Thread> getThreadReference();

    boolean isIdler();

    boolean mayBlock();
}
