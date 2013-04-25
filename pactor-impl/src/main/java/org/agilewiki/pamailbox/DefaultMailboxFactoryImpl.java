package org.agilewiki.pamailbox;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests added to
 * the mailbox in asynchronous mode.
 * </p>
 */

public class DefaultMailboxFactoryImpl<M extends PAMailbox> implements
        PAMailboxFactory {
    private final Logger mailboxLog = LoggerFactory.getLogger(PAMailbox.class);

    private final Logger log = LoggerFactory.getLogger(MailboxFactory.class);

    private final ThreadManager threadManager;
    private final ThreadManager blockingThreadManager;
    private final MessageQueueFactory messageQueueFactory;
    private final Set<AutoCloseable> closables = Collections
            .newSetFromMap(new ConcurrentHashMap<AutoCloseable, Boolean>());
    private final AtomicBoolean shuttingDown = new AtomicBoolean();
    /**
     * How big should the initial local queue size be?
     */
    private final int initialLocalMessageQueueSize;
    /**
     * How big should the initial (per target Mailbox) buffer size be?
     */
    private final int initialBufferSize;

    /**
     * effectively final properties set manager.
     */
    private Properties properties;

    public DefaultMailboxFactoryImpl() {
        this(null, null, MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE);
    }

    public DefaultMailboxFactoryImpl(final ThreadManager blockingThreadManager) {
        this(blockingThreadManager, null,
                MessageQueue.INITIAL_LOCAL_QUEUE_SIZE,
                MessageQueue.INITIAL_BUFFER_SIZE);
    }

    public DefaultMailboxFactoryImpl(final ThreadManager blockingThreadManager,
            final MessageQueueFactory messageQueueFactory,
            final int initialLocalMessageQueueSize, final int initialBufferSize) {
        this.threadManager = ThreadManagerImpl.newThreadManager(Runtime
                .getRuntime().availableProcessors() + 1);
        this.blockingThreadManager = (blockingThreadManager == null) ? new ExecutorServiceWrapper(
                Executors.newCachedThreadPool()) : blockingThreadManager;
        this.messageQueueFactory = (messageQueueFactory == null) ? new DefaultMessageQueueFactoryImpl()
                : messageQueueFactory;
        this.initialLocalMessageQueueSize = initialLocalMessageQueueSize;
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public final M createMailbox() {
        return createMailbox(false, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock) {
        return createMailbox(_mayBlock, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock, final Runnable _onIdle) {
        return createMailbox(_mayBlock, _onIdle, null,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    @Override
    public final M createMailbox(final int initialBufferSize) {
        return createMailbox(false, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock,
            final int initialBufferSize) {
        return createMailbox(_mayBlock, initialBufferSize, null);
    }

    @Override
    public final M createMailbox(final boolean _mayBlock,
            final int initialBufferSize, final Runnable _onIdle) {
        return createMailbox(_mayBlock, _onIdle, null,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    @Override
    public final M createThreadBoundMailbox(final Runnable _messageProcessor) {
        return createMailbox(true, null, _messageProcessor,
                messageQueueFactory
                        .createMessageQueue(initialLocalMessageQueueSize),
                mailboxLog, initialBufferSize);
    }

    public final M createMailbox(final boolean _mayBlock,
            final Runnable _onIdle, final MessageQueue messageQueue) {
        return createMailbox(_mayBlock, _onIdle, null, messageQueue,
                mailboxLog, initialBufferSize);
    }

    @Override
    public final void submit(final Runnable task, final boolean willBlock)
            throws Exception {
        try {
            (willBlock ? blockingThreadManager : threadManager).process(task);
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

    /**
     * Actually instantiate the Mailbox.
     * Can be overridden, to create application-specific Mailbox instances.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected M createMailbox(final boolean _mayBlock, final Runnable _onIdle,
            final Runnable _messageProcessor, final MessageQueue messageQueue,
            final Logger _log, final int _initialBufferSize) {
        return (M) new MailboxImpl(_mayBlock, _onIdle, _messageProcessor, this,
                messageQueue, _log, _initialBufferSize);
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
