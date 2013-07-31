package org.agilewiki.jactor2.core;

/**
 * A mailbox for actors which may process messages requiring either long computations or which may block the thread,
 * or when requests need to be processed atomically.
 * Buffered messages are flushed to their target mailboxes after incoming message is processed,
 * the active thread migrating to the target mailbox of the last buffered message.
 */
public interface AtomicMailbox extends Mailbox {
}
