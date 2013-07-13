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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests added to
 * the mailbox in asynchronous mode.
 * </p>
 */

public class DefaultMailboxFactoryImpl implements
        JAMailboxFactory {
    protected final Logger mailboxLog = LoggerFactory.getLogger(Mailbox.class);

    private final Logger log = LoggerFactory.getLogger(MailboxFactory.class);

    private final ThreadManager nonBlockingThreadManager;
    private final ThreadManager mayBlockThreadManager;
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
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                20);
    }

    public DefaultMailboxFactoryImpl(final int mayBlockThreadCount) {
        this(
                null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                mayBlockThreadCount);
    }

    public DefaultMailboxFactoryImpl(final ThreadManager mayBlockThreadManager) {
        this(
                null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE,
                0);
    }

    public DefaultMailboxFactoryImpl(final MessageQueueFactory _messageQueueFactory,
                                     final int _initialLocalMessageQueueSize,
                                     final int _initialBufferSize,
                                     final int _mayBlockThreadCount) {
        if (_mayBlockThreadCount == 0) {
            nonBlockingThreadManager = ThreadManagerImpl.newThreadManager(Runtime
                    .getRuntime().availableProcessors() + 1);
            mayBlockThreadManager = nonBlockingThreadManager;
        } else if (_mayBlockThreadCount > 0) {
            nonBlockingThreadManager = ThreadManagerImpl.newThreadManager(Runtime
                    .getRuntime().availableProcessors() + 1);
            mayBlockThreadManager = ThreadManagerImpl.newThreadManager(
                    _mayBlockThreadCount);
        } else {
            mayBlockThreadManager = ThreadManagerImpl.newThreadManager(
                    -_mayBlockThreadCount);
            nonBlockingThreadManager = mayBlockThreadManager;
        }
        messageQueueFactory = (_messageQueueFactory == null) ? new DefaultMessageQueueFactoryImpl()
                : _messageQueueFactory;
        initialLocalMessageQueueSize = _initialLocalMessageQueueSize;
        initialBufferSize = _initialBufferSize;
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox() {
        return createNonBlockingMailbox(initialBufferSize, null);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final int initialBufferSize) {
        return createNonBlockingMailbox(initialBufferSize, null);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final Runnable _onIdle) {
        return createNonBlockingMailbox(
                _onIdle,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    @Override
    public final NonBlockingMailboxImpl createNonBlockingMailbox(final int initialBufferSize,
                                                                 final Runnable _onIdle) {
        return createNonBlockingMailbox(_onIdle,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    public final NonBlockingMailboxImpl createNonBlockingMailbox(final Runnable _onIdle,
                                                                 final MessageQueue messageQueue) {
        return createNonBlockingMailbox(_onIdle, messageQueue,
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
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    @Override
    public final MayBlockMailboxImpl createMayBlockMailbox(final int initialBufferSize,
                                                           final Runnable _onIdle) {
        return createMayBlockMailbox(
                _onIdle,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                initialBufferSize);
    }

    public final MayBlockMailboxImpl createMayBlockMailbox(final Runnable _onIdle,
                                                           final MessageQueue messageQueue) {
        return createMayBlockMailbox(_onIdle, messageQueue,
                initialBufferSize);
    }

    @Override
    public final void submit(final UnboundMailbox mailbox, final boolean _mayBlock)
            throws Exception {
        try {
            (_mayBlock ? mayBlockThreadManager : nonBlockingThreadManager).execute(mailbox);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected NonBlockingMailboxImpl createNonBlockingMailbox(
            final Runnable _onIdle,
            final MessageQueue _messageQueue,
            final int _initialBufferSize) {
        return new NonBlockingMailboxImpl(_onIdle, this, _messageQueue, mailboxLog, _initialBufferSize);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected MayBlockMailboxImpl createMayBlockMailbox(
            final Runnable _onIdle,
            final MessageQueue _messageQueue,
            final int _initialBufferSize) {
        return new MayBlockMailboxImpl(_onIdle, this, _messageQueue, mailboxLog, _initialBufferSize);
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
