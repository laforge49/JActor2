package org.agilewiki.pactor.impl;

/**
 * Creates MessageQueues.
 *
 * @author monster
 */
public interface MessageQueueFactory {
    /** Creates a new MessageQueue instance. */
    MessageQueue createMessageQueue();
}
