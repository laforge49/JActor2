package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Message;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class Outbox implements AutoCloseable {

    /**
     * Default initial (per target MessageProcessor) buffer.
     */
    public final static int DEFAULT_INITIAL_BUFFER_SIZE = 16;

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialBufferSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    private Map<MessageProcessorBase, ArrayDeque<Message>> sendBuffer;

    /**
     * The context of this processing.
     */
    protected final JAContext jaContext;

    /**
     * Create an outbox.
     *
     * @param _initialBufferSize Initial size of each outbox per target MessageProcessor.
     */
    public Outbox(final JAContext _jaContext, final int _initialBufferSize) {
        jaContext = _jaContext;
        initialBufferSize = _initialBufferSize;
    }

    public Iterator<Map.Entry<MessageProcessorBase, ArrayDeque<Message>>> getIterator() {
        if (sendBuffer == null)
            return null;
        return sendBuffer.entrySet().iterator();
    }

    /**
     * Buffers a message in the sending processing for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The processing that should eventually receive this message
     * @return True if the message was buffered.
     */
    public boolean buffer(final Message _message, final MessageProcessor _target) {
        if (jaContext.isClosing())
            return false;
        ArrayDeque<Message> buffer = null;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<MessageProcessorBase, ArrayDeque<Message>>();
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialBufferSize);
            sendBuffer.put((MessageProcessorBase) _target, buffer);
        }
        buffer.add(_message);
        return true;
    }

    @Override
    public void close() {
        final Iterator<Map.Entry<MessageProcessorBase, ArrayDeque<Message>>> iter = getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                final Map.Entry<MessageProcessorBase, ArrayDeque<Message>> entry = iter.next();
                final MessageProcessorBase target = entry.getKey();
                if (target.getJAContext() != jaContext) {
                    final ArrayDeque<Message> messages = entry.getValue();
                    iter.remove();
                    try {
                        target.unbufferedAddMessages(messages);
                    } catch (Exception x) {
                    }
                } else
                    iter.remove();
            }
        }
        sendBuffer = null;
    }
}
