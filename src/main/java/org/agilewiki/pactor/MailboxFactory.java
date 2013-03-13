package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.MailboxImpl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailboxFactory {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<AutoCloseable> closables;

    public Mailbox createMailbox() {
        return new MailboxImpl(this);
    }

    public void submit(Runnable task) {
        executorService.submit(task);
    }

    public void addAutoClosable(AutoCloseable closeable) {
        closables.add(closeable);
    }

    public void shutdown() {
        Iterator<AutoCloseable> it = closables.iterator();
        while (it.hasNext()) {
            try {
                it.next().close();
            } catch (Throwable t) {
            }
        }
        executorService.shutdownNow();
    }
}
