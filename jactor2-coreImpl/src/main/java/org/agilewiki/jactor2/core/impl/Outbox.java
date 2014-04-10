package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.reactorsImpl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An outbox holds a collection of send buffers.
 * Each send buffer holds one or more messages, all destined for the same reactor.
 */
public class Outbox implements AutoCloseable {

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialBufferSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    private Map<ReactorImpl, ArrayDeque<RequestImpl>> sendBuffer;

    /**
     * Create an Outbox
     *
     * @param _initialBufferSize    Initial size of a send buffer.
     */
    public Outbox(final int _initialBufferSize) {
        initialBufferSize = _initialBufferSize;
    }

    /**
     * Returns an iterator of the send buffers held by the outbox.
     *
     * @return An iterator of the send buffers held by the outbox.
     */
    public Iterator<Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>>> getIterator() {
        if (sendBuffer == null) {
            return null;
        }
        return sendBuffer.entrySet().iterator();
    }

    /**
     * Buffers a message in the sending reactor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The reactor that should eventually receive this message.
     * @return True if the message was successfully buffered.
     */
    public boolean buffer(final RequestImpl _message, final ReactorImpl _target) {
        if (_target.isClosing())
            return false;
        ArrayDeque<RequestImpl> buffer = null;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<ReactorImpl, ArrayDeque<RequestImpl>>();
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<RequestImpl>(initialBufferSize);
            sendBuffer.put(_target, buffer);
        }
        buffer.add(_message);
        return true;
    }

    /**
     * Forwards all the buffered messages.
     */
    @Override
    public void close() {
        final Iterator<Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>>> iter = getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                final Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>> entry = iter
                        .next();
                final ReactorImpl target = entry.getKey();
                    final ArrayDeque<RequestImpl> messages = entry.getValue();
                    iter.remove();
                    try {
                        target.unbufferedAddMessages(messages);
                    } catch (final Exception x) {
                    }
            }
        }
        sendBuffer = null;
    }
}
