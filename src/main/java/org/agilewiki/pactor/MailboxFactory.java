package org.agilewiki.pactor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.agilewiki.pactor.impl.MailboxImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The MailboxFactory is the factory as the name suggests for the MailBoxes to be used with the PActor. In addition to 
 * creation of the Mailboxes it also encapsulates the threads( threadpool) which would process the Requests coming to 
 * the mailbox in asynchronous mode.
 * </p>
 */

public final class MailboxFactory {
	private static Logger LOG = LoggerFactory.getLogger(MailboxFactory.class);;
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
            else            
            	LOG.warn("Unable to process the request, possible mailbox shutdown had been called in the application");
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
