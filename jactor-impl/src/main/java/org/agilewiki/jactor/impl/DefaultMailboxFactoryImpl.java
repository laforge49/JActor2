package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.api.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests added to
 * the mailbox in asynchronous mode.
 * </p>
 */

public class DefaultMailboxFactoryImpl<M extends JAMailbox> implements
        JAMailboxFactory {
    protected final Logger mailboxLog = LoggerFactory.getLogger(JAMailbox.class);

    private final Logger log = LoggerFactory.getLogger(MailboxFactory.class);

    private final ThreadManager threadManager;
    private final ThreadManager blockingThreadManager;
    protected final MessageQueueFactory messageQueueFactory;
    private final Set<AutoCloseable> closables = Collections
            .newSetFromMap(new ConcurrentHashMap<AutoCloseable, Boolean>());
    private final AtomicBoolean shuttingDown = new AtomicBoolean();
    /**
     * How big should the initial local queue size be?
     */
    protected final int initialLocalMessageQueueSize;
    /**
     * How big should the initial (per target Mailbox) buffer size be?
     */
    protected final int initialBufferSize;

    /**
     * effectively final properties set manager.
     */
    private Properties properties;

    public DefaultMailboxFactoryImpl() {
        this(
                null,
                null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                20);
    }

    public DefaultMailboxFactoryImpl(final int maxBlockingThreads) {
        this(
                null,
                null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                maxBlockingThreads);
    }

    public DefaultMailboxFactoryImpl(final ThreadManager blockingThreadManager) {
        this(
                blockingThreadManager,
                null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                0);
    }

    public DefaultMailboxFactoryImpl(final ThreadManager blockingThreadManager,
                                     final MessageQueueFactory messageQueueFactory,
                                     final int initialLocalMessageQueueSize,
                                     final int initialBufferSize, final int maxBlockingThreads) {
        this.threadManager = ThreadManagerImpl.newThreadManager(Runtime
                .getRuntime().availableProcessors() + 1);
        this.blockingThreadManager = (blockingThreadManager == null) ? ThreadManagerImpl.newThreadManager(
                maxBlockingThreads) : blockingThreadManager;
        this.messageQueueFactory = (messageQueueFactory == null) ? new DefaultMessageQueueFactoryImpl()
                : messageQueueFactory;
        this.initialLocalMessageQueueSize = initialLocalMessageQueueSize;
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox() {
        return (NonBlockingMailboxImpl) createMailbox(false, initialBufferSize, null);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox() {
        return (MayBlockMailboxImpl) createMailbox(true, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock, final Runnable _onIdle) {
        return createMailbox(_mayBlock, _onIdle,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final int initialBufferSize) {
        return (NonBlockingMailboxImpl) createMailbox(false, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock,
                                 final int initialBufferSize) {
        return createMailbox(_mayBlock, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock,
                                 final int initialBufferSize, final Runnable _onIdle) {
        return createMailbox(_mayBlock, _onIdle,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    public final M createMailbox(final boolean _mayBlock,
                                 final Runnable _onIdle, final MessageQueue messageQueue) {
        return createMailbox(_mayBlock, _onIdle, messageQueue,
                mailboxLog, initialBufferSize);
    }

    @Override
    public final void submit(final JAMailbox mailbox, final boolean willBlock)
            throws Exception {
        try {
            (willBlock ? blockingThreadManager : threadManager).execute(mailbox);
        } catch (final Exception e) {
            if (!isClosing())
                throw e;
            else
                log.warn(
                        "Unable to process the request, as mailbox shutdown had been called in the application",
                        e);
        } catch (final Error e) {
            if (!isClosing())
                throw e;
        }
    }

    @Override
    public final boolean addAutoClosable(final AutoCloseable closeable) {
        if (!isClosing()) {
            return closables.add(closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    @Override
    public final boolean removeAutoClosable(final AutoCloseable closeable) {
        if (!isClosing()) {
            return closables.remove(closeable);
        } else {
            throw new IllegalStateException("Shuting down ...");
        }
    }

    @Override
    public final void close() throws Exception {
        if (shuttingDown.compareAndSet(false, true)) {
            threadManager.close();
            blockingThreadManager.close();
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Deprecated
    protected M createMailbox(final boolean _mayBlock, final Runnable _onIdle,
                              final MessageQueue messageQueue,
                              final Logger _log, final int _initialBufferSize) {
        return (M) (_mayBlock ?
                new MayBlockMailboxImpl(_onIdle, this, messageQueue, _log, _initialBufferSize) :
                new NonBlockingMailboxImpl(_onIdle, this, messageQueue, _log, _initialBufferSize));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected NonBlockingMailboxImpl createNonBlockingMailbox(
            final Runnable _onIdle,
            final MessageQueue messageQueue,
            final Logger _log, final int _initialBufferSize) {
        return new NonBlockingMailboxImpl(_onIdle, this, messageQueue, _log, _initialBufferSize);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected MayBlockMailboxImpl createMayBlockMailbox(
            final Runnable _onIdle,
            final MessageQueue messageQueue,
            final Logger _log, final int _initialBufferSize) {
        return new MayBlockMailboxImpl(_onIdle, this, messageQueue, _log, _initialBufferSize);
    }

    @Override
    public final ThreadBoundMailboxImpl createThreadBoundMailbox(final Runnable _messageProcessor) {
        return new ThreadBoundMailboxImpl(_messageProcessor, this,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
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
