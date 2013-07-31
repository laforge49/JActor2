package org.agilewiki.jactor2.core.context;

import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;
import org.agilewiki.jactor2.core.mailbox.ThreadBoundMailbox;

/**
 * The extended JAContext interface for use in the implementation.
 */
public interface JAContext extends AutoCloseable {

    NonBlockingMailbox createNonBlockingMailbox();

    NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize);

    NonBlockingMailbox createNonBlockingMailbox(final Runnable _onIdle);

    NonBlockingMailbox createNonBlockingMailbox(final int _initialBufferSize,
                                                final Runnable _onIdle);

    AtomicMailbox createAtomicMailbox();

    AtomicMailbox createAtomicMailbox(final int _initialBufferSize);

    AtomicMailbox createAtomicMailbox(final Runnable _onIdle);

    AtomicMailbox createAtomicMailbox(final int _initialBufferSize,
                                      final Runnable _onIdle);

    ThreadBoundMailbox createThreadBoundMailbox(final Runnable _messageProcessor);

    boolean addAutoClosable(final AutoCloseable _closeable);

    boolean removeAutoClosable(final AutoCloseable _closeable);

    boolean isClosing();

    void setProperties(final Properties _properties);

    Properties getProperties();

    void submit(final Mailbox _mailbox)
            throws Exception;
}
