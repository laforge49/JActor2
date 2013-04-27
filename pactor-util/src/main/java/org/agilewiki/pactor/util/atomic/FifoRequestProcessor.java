package org.agilewiki.pactor.util.atomic;

import java.util.ArrayDeque;
import java.util.Queue;

import org.agilewiki.pactor.api.MailboxFactory;

/**
 * Create an atomic request processor which processes requests successively and in
 * the order received.
 */
public class FifoRequestProcessor extends AtomicRequestProcessor {
    /**
     * Creates a FifoRequestProcessor.
     *
     * @param _mailboxFactory The mailbox factory.
     * @return A new FifoRequestProcessor.
     */
    public static FifoRequestProcessor create(
            final MailboxFactory _mailboxFactory) throws Exception {
        FifoRequestProcessor arp = new FifoRequestProcessor();
        arp.initialize(_mailboxFactory.createMailbox(false, arp));
        return arp;
    }

    @Override
    protected Queue<AtomicEntry> createQueue() {
        return new ArrayDeque<AtomicEntry>();
    }
}
