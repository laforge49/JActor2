package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.api.Properties;
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
        JAMailboxFactory {

    /**
     * The logger used by mailboxes.
     */
    protected final Logger mailboxLogger = LoggerFactory.getLogger(Mailbox.class);

    /**
     * The mailbox factory logger.
     */
    private final Logger logger = LoggerFactory.getLogger(MailboxFactory.class);

    /**
     * The thread pool used by non-blocking mailboxes.
     */
    private final ThreadManager nonBlockingThreadManager;

    /**
     * The thread pool used by may-block mailboxes.
     */
    private final ThreadManager mayBlockThreadManager;

    /**
     * The inbox factory used when creating mailboxes.
     */
    protected final InboxFactory inboxFactory;

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
     * Create a mailbox factory.
     */
    public DefaultMailboxFactory() {
        this(
                null,
                Inbox.INITIAL_LOCAL_QUEUE_SIZE,
                Inbox.INITIAL_BUFFER_SIZE,
                20,
                new DefaultThreadFactory());
    }

    /**
     * Create a mailbox factory with one or two threadpools.
     * If the mayBlockThreadCount is == 0, then a common thread pool is created
     * with a size equal to the number of hardware threads + 1.
     * If the mayBlockThreadCount is < 0, then a common thread pool is created
     * with a size = - mayBlockThreadCount.
     * Otherwise a non-blocking thread pool is created
     * with a size equal to the number of hardware threads + 1 and a may-block thread pool is created
     * with a size = mayBlockThreadCount.
     *
     * @param _mayBlockThreadCount The thread pool size for mailboxes that may block.
     */
    public DefaultMailboxFactory(final int _mayBlockThreadCount) {
        this(
                null,
                Inbox.INITIAL_LOCAL_QUEUE_SIZE,
                Inbox.INITIAL_BUFFER_SIZE,
                _mayBlockThreadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a mailbox factory with one or two threadpools.
     * If the mayBlockThreadCount is == 0, then a common thread pool is created
     * with a size equal to the number of hardware threads + 1.
     * If the mayBlockThreadCount is < 0, then a common thread pool is created
     * with a size = - mayBlockThreadCount.
     * Otherwise a non-blocking thread pool is created
     * with a size equal to the number of hardware threads + 1 and a may-block thread pool is created
     * with a size = mayBlockThreadCount.
     *
     * @param _inboxFactory                 The inbox factory used when creating mailboxes.
     * @param _initialLocalMessageQueueSize How big should the initial inbox local queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Mailbox) buffer size be?
     * @param _mayBlockThreadCount          The thread pool size for mailboxes that may block.
     */
    public DefaultMailboxFactory(final InboxFactory _inboxFactory,
                                 final int _initialLocalMessageQueueSize,
                                 final int _initialBufferSize,
                                 final int _mayBlockThreadCount,
                                 final ThreadFactory _threadFactory) {
        if (_mayBlockThreadCount == 0) {
            nonBlockingThreadManager = ThreadManager.newThreadManager(Runtime
                    .getRuntime().availableProcessors() + 1, _threadFactory);
            mayBlockThreadManager = nonBlockingThreadManager;
        } else if (_mayBlockThreadCount > 0) {
            nonBlockingThreadManager = ThreadManager.newThreadManager(Runtime
                    .getRuntime().availableProcessors() + 1, _threadFactory);
            mayBlockThreadManager = ThreadManager.newThreadManager(
                    _mayBlockThreadCount, _threadFactory);
        } else {
            mayBlockThreadManager = ThreadManager.newThreadManager(
                    -_mayBlockThreadCount, _threadFactory);
            nonBlockingThreadManager = mayBlockThreadManager;
        }
        inboxFactory = (_inboxFactory == null) ? new DefaultInboxFactory()
                : _inboxFactory;
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox() {
        return createNonBlockingMailbox(initialBufferSize, null);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final int _initialBufferSize) {
        return createNonBlockingMailbox(_initialBufferSize, null);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final Runnable _onIdle) {
        return createNonBlockingMailbox(
                _onIdle,
                inboxFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final int _initialBufferSize,
                                                                 final Runnable _onIdle) {
        return createNonBlockingMailbox(_onIdle,
                inboxFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @param _inbox  The inbox to be used by the new mailbox.
     * @return A new non-blocking mailbox.
     */
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final Runnable _onIdle,
                                                                 final Inbox _inbox) {
        return createNonBlockingMailbox(_onIdle, _inbox,
                initialBufferSize);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox() {
        return createMayBlockMailbox(initialBufferSize, null);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox(final int initialBufferSize) {
        return createMayBlockMailbox(initialBufferSize, null);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox(final Runnable _onIdle) {
        return createMayBlockMailbox(
                _onIdle,
                inboxFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox(final int initialBufferSize,
                                                           final Runnable _onIdle) {
        return createMayBlockMailbox(
                _onIdle,
                inboxFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @param _onIdle The run method is called when the input queue is empty.
     * @param _inbox  The inbox to be used by the new mailbox.
     * @return A new may-block mailbox.
     */
    public final MayBlockMailboxImpl createMayBlockMailbox(final Runnable _onIdle,
                                                           final Inbox _inbox) {
        return createMayBlockMailbox(_onIdle, _inbox,
                initialBufferSize);
    }

    @Override
    public final void submit(final UnboundMailbox _mailbox, final boolean _mayBlock)
            throws Exception {
        try {
            (_mayBlock ? mayBlockThreadManager : nonBlockingThreadManager).execute(_mailbox);
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

    @Override
    public final boolean addAutoClosable(final AutoCloseable _closeable) {
        if (!isClosing()) {
            return closables.add(_closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    @Override
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
            nonBlockingThreadManager.close();
            mayBlockThreadManager.close();
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

    @Override
    public final boolean isClosing() {
        return shuttingDown.get();
    }

    /**
     * Creates a Mailbox which is only used to process non-blocking requests.
     *
     * @param _onIdle            The run method is called when the input queue is empty.
     * @param _inbox             The inbox to be used by the new mailbox.
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new non-blocking mailbox.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected NonBlockingMailboxImpl createNonBlockingMailbox(
            final Runnable _onIdle,
            final Inbox _inbox,
            final int _initialBufferSize) {
        return new NonBlockingMailboxImpl(_onIdle, this, _inbox, mailboxLogger, _initialBufferSize);
    }

    /**
     * Creates a Mailbox for processing messages that perform long computations or which may block the thread.
     *
     * @param _onIdle            The run method is called when the input queue is empty.
     * @param _inbox             The inbox to be used by the new mailbox.
     * @param _initialBufferSize How big should the initial (per target Mailbox) buffer size be?
     * @return A new may-block mailbox.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected MayBlockMailboxImpl createMayBlockMailbox(
            final Runnable _onIdle,
            final Inbox _inbox,
            final int _initialBufferSize) {
        return new MayBlockMailboxImpl(_onIdle, this, _inbox, mailboxLogger, _initialBufferSize);
    }

    @Override
    public final ThreadBoundMailboxImpl createThreadBoundMailbox(final Runnable _messageProcessor) {
        return new ThreadBoundMailboxImpl(_messageProcessor, this,
                inboxFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLogger, initialBufferSize);
    }

    @Override
    public void setProperties(final Properties _properties) {
        if (properties != null)
            throw new IllegalStateException("properties has already been set");
        properties = _properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
