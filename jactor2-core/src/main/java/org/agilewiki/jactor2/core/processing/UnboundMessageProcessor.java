package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.messaging.Message;
import org.agilewiki.jactor2.core.threading.MigrationException;
import org.agilewiki.jactor2.core.threading.ModuleContext;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common code for NonBlockingMessageProcessor and IsolationMessageProcessor, which are not bound to a thread.
 * <p>
 * UnboundMessageProcessor supports thread migration only between instances of this class.
 * </p>
 */
abstract public class UnboundMessageProcessor extends MessageProcessorBase {

    /**
     * A reference to the thread that is executing this message processor.
     */
    protected final AtomicReference<Thread> threadReference = new AtomicReference<Thread>();

    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    private final Runnable onIdle;

    /**
     * Create a message processor.
     *
     * @param _context               The context of this message processor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public UnboundMessageProcessor(ModuleContext _context,
                                   int _initialOutboxSize,
                                   final int _initialLocalQueueSize,
                                   Runnable _onIdle) {
        super(_context, _initialOutboxSize, _initialLocalQueueSize);
        onIdle = _onIdle;
    }

    @Override
    public AtomicReference<Thread> getThreadReference() {
        return threadReference;
    }

    @Override
    public boolean isIdler() {
        return onIdle != null;
    }

    @Override
    protected void notBusy() throws Exception {
        if (onIdle != null && inbox.isIdle()) {
            flush(true);
            onIdle.run();
        }
        flush(true);
    }

    @Override
    public boolean isRunning() {
        return threadReference.get() != null;
    }

    @Override
    protected void afterAdd() throws Exception {
        if (threadReference.get() == null) {
            moduleContext.submit(this);
        }
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @param _mayMigrate True when thread migration is allowed.
     * @return True when one or more buffered request/result was delivered.
     */
    public boolean flush(boolean _mayMigrate) throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<MessageProcessorBase, ArrayDeque<Message>>> iter = outbox.getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<MessageProcessorBase, ArrayDeque<Message>> entry = iter.next();
                final MessageProcessorBase target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() &&
                        _mayMigrate &&
                        getModuleContext() == target.getModuleContext() &&
                        target instanceof UnboundMessageProcessor) {
                    if (!target.isRunning()) {
                        Thread currentThread = threadReference.get();
                        MessageProcessorBase targ = target;
                        AtomicReference<Thread> targetThreadReference = targ.getThreadReference();
                        if (targetThreadReference.get() == null &&
                                targetThreadReference.compareAndSet(null, currentThread)) {
                            while (!messages.isEmpty()) {
                                Message m = messages.poll();
                                targ.unbufferedAddMessage(m, true);
                            }
                            throw new MigrationException(targ);
                        }
                    }
                }
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
