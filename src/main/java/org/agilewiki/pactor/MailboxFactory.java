package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.MailboxImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MailboxFactory {
    private final ExecutorService executorService = Executors
            .newCachedThreadPool();
    private final List<AutoCloseable> closables = new ArrayList<AutoCloseable>();
    private boolean shuttingDown;

    public Mailbox createMailbox() {
        return new MailboxImpl(this);
    }

    public void submit(final Runnable task) throws Exception {
        try {
            executorService.submit(task);
        } catch (final Exception e) {
            if (!shuttingDown)
                throw e;
        } catch (final Error e) {
            if (!shuttingDown)
                throw e;
        }
    }

    public void addAutoClosable(final AutoCloseable closeable) {
        closables.add(closeable);
    }

    public void shutdown() {
        shuttingDown = true;
        final Iterator<AutoCloseable> it = closables.iterator();
        while (it.hasNext()) {
            try {
                it.next().close();
            } catch (final Throwable t) {
            }
        }
        executorService.shutdownNow();
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }
}
