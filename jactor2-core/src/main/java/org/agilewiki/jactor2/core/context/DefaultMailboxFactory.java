package org.agilewiki.jactor2.core.context;

import org.agilewiki.jactor2.core.mailbox.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Creates non-blocking, may-block and thread-bound mailboxes and serves as a thread pool for
 * non-blocking and may-block mailboxes. Multiple mailbox factories with independent life cycles
 * are also supported.
 * In addition, the mailbox factory maintains a set of AutoClosable objects that are closed at
 * the end-of-life of the mailbox factory as well as a set of properties.
 */

public class DefaultMailboxFactory implements
        JAContext {

    /**
     * The logger used by mailboxes.
     */
    protected final Logger mailboxLogger = LoggerFactory.getLogger(Mailbox.class);

    /**
     * The mailbox factory logger.
     */
    private final Logger logger = LoggerFactory.getLogger(JAContext.class);

    /**
     * The thread pool used by mailboxes.
     */
    private final ThreadManager threadManager;

    /**
     * A hash set of AutoCloseable objects.
     */
    private final Set<AutoCloseable> closables = Collections
            .newSetFromMap(new ConcurrentHashMap<AutoCloseable, Boolean>());

    /**
     * Set when the mailbox factory reaches end-of-life.
     */
    private final AtomicBoolean shuttingDown = new AtomicBoolean();

    /**
     * How big should the initial inbox local queue size be?
     */
    protected final int initialLocalMessageQueueSize;

    /**
     * How big should the initial outbox (per target Mailbox) buffer size be?
     */
    protected final int initialBufferSize;

    /**
     * Mailbox factory properties.
     */
    private Properties properties;

    /**
     * Create a mailbox factory and a threadpool.
     */
    public DefaultMailboxFactory() {
        this(
                Inbox.INITIAL_LOCAL_QUEUE_SIZE,
                Inbox.INITIAL_BUFFER_SIZE,
                20,
                new DefaultThreadFactory());
    }

    /**
     * Create a mailbox factory and a threadpool.
     *
     * @param _threadCount The thread pool size for mailboxes.
     */
    public DefaultMailboxFactory(final int _threadCount) {
        this(
                Inbox.INITIAL_LOCAL_QUEUE_SIZE,
                Inbox.INITIAL_BUFFER_SIZE,
                _threadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a mailbox factory and a threadpool.
     *
     * @param _initialLocalMessageQueueSize How big should the initial inbox local queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Mailbox) buffer size be?
     * @param _threadCount                  The thread pool size for mailboxes.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    public DefaultMailboxFactory(final int _initialLocalMessageQueueSize,
                                 final int _initialBufferSize,
                                 final int _threadCount,
                                 final ThreadFactory _threadFactory) {
        threadManager = ThreadManager.newThreadManager(
                _threadCount, _threadFactory);
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking messages.
     *
     * @return A new non-blocking mailbox.
     */
    public final NonBlockingMailbox createNonBlockingMailbox() {
        return createNonBlockingMailbox(initialBufferSize, null);
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new non-blocking mailbox.
     */
    public final NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize) {
        return createNonBlockingMailbox(_initialBufferSize, null);
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    public final NonBlockingMailbox createNonBlockingMailbox(final Runnable _onIdle) {
        return createNonBlockingMailbox(initialBufferSize, _onIdle);
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param _onIdle            The run method is called when the input queue is empty.
     * @return A new non-blocking mailbox.
     */
    public final NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize,
                                                             final Runnable _onIdle) {
        return new NonBlockingMailbox(_onIdle, this, mailboxLogger, _initialBufferSize, initialLocalMessageQueueSize);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @return A new atomic mailbox.
     */
    public final AtomicMailbox createAtomicMailbox() {
        return createAtomicMailbox(initialBufferSize, null);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new atomic mailbox.
     */
    public final AtomicMailbox createAtomicMailbox(final int _initialBufferSize) {
        return createAtomicMailbox(_initialBufferSize, null);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @return A new atomic mailbox.
     */
    public final AtomicMailbox createAtomicMailbox(final Runnable _onIdle) {
        return createAtomicMailbox(initialBufferSize, _onIdle);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations
     * or which may block the thread, or when requests must be processed atomically.
     *
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @param _onIdle            The run method is called when the input queue is empty.
     * @return A new atomic mailbox.
     */
    public final AtomicMailbox createAtomicMailbox(final int _initialBufferSize,
                                                   final Runnable _onIdle) {
        return new AtomicMailbox(_onIdle, this, mailboxLogger, _initialBufferSize, initialLocalMessageQueueSize);
    }

    /**
     * Submit a mailbox for subsequent execution.
     *
     * @param _mailbox The mailbox to be run.
     */
    public final void submit(final Mailbox _mailbox)
            throws Exception {
        try {
            threadManager.execute(_mailbox);
        } catch (final Exception e) {
            if (!isClosing())
                throw e;
            else
                logger.warn(
                        "Unable to process the request, as mailbox shutdown had been called in the application",
                        e);
        } catch (final Error e) {
            if (!isClosing())
                throw e;
        }
    }

    /**
     * Adds an auto closeable, to be closed when the JAContext closes.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return True, if the list was updated.
     */
    public final boolean addAutoClosable(final AutoCloseable _closeable) {
        if (!isClosing()) {
            return closables.add(_closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    /**
     * Remove an auto closeable from the list of closables.
     *
     * @param _closeable The autoclosable to be removed from the list.
     * @return True, if the list was updated.
     */
    public final boolean removeAutoClosable(final AutoCloseable _closeable) {
        if (!isClosing()) {
            return closables.remove(_closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    @Override
    public final void close() throws Exception {
        if (shuttingDown.compareAndSet(false, true)) {
            threadManager.close();
            final Iterator<AutoCloseable> it = closables.iterator();
            while (it.hasNext()) {
                try {
                    it.next().close();
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns true if close() has been called already.
     *
     * @return true if close() has already been called.
     */
    public final boolean isClosing() {
        return shuttingDown.get();
    }

    /**
     * Creates a mailbox that runs on an existing thread.
     *
     * @param _messageProcessor The run method is called when there are messages
     *                          to be processed.
     * @return The thread bounded mailbox.
     */
    public final ThreadBoundMailbox createThreadBoundMailbox(final Runnable _messageProcessor) {
        return new ThreadBoundMailbox(_messageProcessor, this,
                mailboxLogger, initialBufferSize, initialLocalMessageQueueSize);
    }

    /**
     * Assigns the effectively final properties set manager.
     * Once assigned, it can not be updated.
     *
     * @param _properties The properties set manager.
     */
    public void setProperties(final Properties _properties) {
        if (properties != null)
            throw new IllegalStateException("properties has already been set");
        properties = _properties;
    }

    /**
     * Returns the property set manager.
     *
     * @return The properties set manager.
     */
    public Properties getProperties() {
        return properties;
    }
}
