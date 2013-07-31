package org.agilewiki.jactor2.core.mailbox;

/**
 * A mailbox for actors which neither block the thread nor perform long computations.
 * Buffered messages are flushed to their target mailboxes when the inbox is empty,
 * the active thread migrating to the target mailbox of the last buffered message.
 */
public interface NonBlockingMailbox extends Mailbox {
}
