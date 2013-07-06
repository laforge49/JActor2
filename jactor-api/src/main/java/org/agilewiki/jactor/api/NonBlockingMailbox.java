package org.agilewiki.jactor.api;

/**
 * A mailbox for actors which neither block the thread nor perform long computations.
 */
public interface NonBlockingMailbox extends Mailbox {
}
