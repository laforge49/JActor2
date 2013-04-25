package org.agilewiki.pamailbox;

import org.agilewiki.pactor.MailboxFactory;

public interface PAMailboxFactory extends MailboxFactory {
    void submit(final Runnable task, final boolean willBlock) throws Exception;

    @Override
    PAMailbox createMailbox();

    @Override
    PAMailbox createMailbox(final boolean mayBlock);

    @Override
    PAMailbox createMailbox(final int initialBufferSize);

    @Override
    PAMailbox createMailbox(final boolean mayBlock, final int initialBufferSize);

    @Override
    PAMailbox createMailbox(final boolean mayBlock, final Runnable onIdle);

    @Override
    PAMailbox createMailbox(final boolean mayBlock,
            final int initialBufferSize, final Runnable onIdle);

    @Override
    PAMailbox createThreadBoundMailbox(final Runnable _messageProcessor);
}
