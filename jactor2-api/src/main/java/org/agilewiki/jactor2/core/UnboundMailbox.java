package org.agilewiki.jactor2.core;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A mailbox that is not bound to a single thread.
 */
public interface UnboundMailbox extends JAMailbox {

    /**
     * Returns the atomic reference to the current thread.
     *
     * @return
     */
    AtomicReference<Thread> getThreadReference();

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    boolean isIdler();
}
