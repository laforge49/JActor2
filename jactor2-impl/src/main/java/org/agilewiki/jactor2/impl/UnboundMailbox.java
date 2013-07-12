package org.agilewiki.jactor2.impl;

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
}
