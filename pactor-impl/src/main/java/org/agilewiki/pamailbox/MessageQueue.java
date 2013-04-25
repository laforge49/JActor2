package org.agilewiki.pamailbox;

import java.util.Queue;

import org.agilewiki.pactor.Actor;
import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pactor._Request;

/**
 * A concurrent message queue, used in the Mailbox.
 *
 * @author monster
 */
public interface MessageQueue {
    /**
     * How big should the initial local queue size be?
     */
    int INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * How big should the initial (per target Mailbox) buffer size be?
     */
    int INITIAL_BUFFER_SIZE = 16;

    /**
     * Creates a new Message instance.
     */
    <E, A extends Actor> Message createMessage(final boolean _foreign,
            final MessageSource _source, final A _targetActor,
            final Message _old, final _Request<E, A> _request,
            final ExceptionHandler _handler, final ResponseProcessor<E> _rp);

    /**
     * Is the queue empty?
     */
    boolean isNonEmpty();

    /**
     * Inserts a new message in the queue.
     *
     * @param local Should be true for same-mailbox exchanges
     * @param msg   The new message
     */
    void offer(final boolean local, final Message msg);

    /**
     * Inserts a new message in the queue.
     * Multi-offer assumes the messages are not local.
     *
     * @param msgs The new messages
     */
    void offer(final Queue<Message> msgs);

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    Message poll();
}
