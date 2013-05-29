package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.MailboxFactory;

public interface JAMailboxFactory extends MailboxFactory {
    void submit(final Runnable task, final boolean willBlock) throws Exception;

    @Override
    JAMailbox createMailbox();

    @Override
    JAMailbox createMailbox(final boolean mayBlock);

    @Override
    JAMailbox createMailbox(final int initialBufferSize);

    @Override
    JAMailbox createMailbox(final boolean mayBlock, final int initialBufferSize);

    @Override
    JAMailbox createMailbox(final boolean mayBlock, final Runnable onIdle);

    @Override
    JAMailbox createMailbox(final boolean mayBlock,
                            final int initialBufferSize, final Runnable onIdle);

    @Override
    JAMailbox createThreadBoundMailbox(final Runnable _messageProcessor);
}
