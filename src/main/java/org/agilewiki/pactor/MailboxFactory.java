package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.MailboxImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MailboxFactory {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<AutoCloseable> closables = new ArrayList<AutoCloseable>();
    private boolean shuttingDown;

    public Mailbox createMailbox() {
        return new MailboxImpl(this);
    }

    public void submit(Runnable task) throws Throwable {
        try {
            executorService.submit(task);
        } catch (Throwable t) {
            if (!shuttingDown)
                throw t;
        }
    }

    public void addAutoClosable(AutoCloseable closeable) {
        closables.add(closeable);
    }

    public void shutdown() {
        shuttingDown = true;
        Iterator<AutoCloseable> it = closables.iterator();
        while (it.hasNext()) {
            try {
                it.next().close();
            } catch (Throwable t) {
            }
        }
        executorService.shutdownNow();
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }
}
