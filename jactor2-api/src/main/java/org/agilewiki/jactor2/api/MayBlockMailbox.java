package org.agilewiki.jactor2.api;

/**
 * A mailbox for actors which may process messages requiring either long computations or which may block the thread.
 * Buffered messages are flushed to their target mailboxes after incoming message is processed,
 * the active thread migrating to the target mailbox of the last buffered message.
 */
public interface MayBlockMailbox extends Mailbox {
}
