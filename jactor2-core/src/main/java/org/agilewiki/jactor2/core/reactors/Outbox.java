package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An outbox holds a collection of doSend buffers.
 * Each doSend buffer holds one or more messages, all destined for the same targetReactor.
 */
public class Outbox implements AutoCloseable {

    /**
     * Default initial (per target Reactor) buffer.
     */
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 16;

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialBufferSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    private Map<ReactorBase, ArrayDeque<Message>> sendBuffer;

    /**
     * The facility of this outbox.
     */
    protected final Facility facility;

    protected final Reactor reactor;

    /**
     * Create an outbox.
     *
     * @param _initialBufferSize Initial size of each outbox per target Reactor.
     */
    public Outbox(final Reactor _reactor, final int _initialBufferSize) {
        reactor = _reactor;
        facility = _reactor.getFacility();
        initialBufferSize = _initialBufferSize;
    }

    /**
     * Returns an iterator of the doSend buffers held by the outbox.
     *
     * @return An iterator of the doSend buffers held by the outbox.
     */
    public Iterator<Map.Entry<ReactorBase, ArrayDeque<Message>>> getIterator() {
        if (sendBuffer == null) {
            return null;
        }
        return sendBuffer.entrySet().iterator();
    }

    /**
     * Buffers a message in the sending targetReactor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The targetReactor that should eventually receive this message.
     * @return True if the message was successfully buffered.
     */
    public boolean buffer(final Message _message, final Reactor _target) {
        if (facility.isClosing()) {
            return false;
        }
        ArrayDeque<Message> buffer = null;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<ReactorBase, ArrayDeque<Message>>();
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialBufferSize);
            sendBuffer.put((ReactorBase) _target, buffer);
        }
        buffer.add(_message);
        return true;
    }

    @Override
    public void close() {
        final Iterator<Map.Entry<ReactorBase, ArrayDeque<Message>>> iter = getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                final Map.Entry<ReactorBase, ArrayDeque<Message>> entry = iter
                        .next();
                final ReactorBase target = entry.getKey();
                if (target.getFacility() != facility) {
                    final ArrayDeque<Message> messages = entry.getValue();
                    iter.remove();
                    try {
                        target.unbufferedAddMessages(messages);
                    } catch (final Exception x) {
                    }
                } else {
                    iter.remove();
                }
            }
        }
        sendBuffer = null;
    }
}
