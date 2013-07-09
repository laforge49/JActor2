package org.agilewiki.jactor2.api;

/**
 * A mailbox for actors which may process messages requiring either long computations or which may block the thread.
 */
public interface MayBlockMailbox extends Mailbox {
}
