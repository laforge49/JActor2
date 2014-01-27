package org.agilewiki.jactor2.core.closeable;

import java.util.concurrent.Future;

/**
 * Cancels a future when a dependent reactor is closed.
 */
public class FutureCloser implements Closeable {
    private final CloseableImpl closeableImpl;
    private final Future future;

    /**
     * Create a future closer.
     *
     * @param _future The future to be closed.
     */
    public FutureCloser(final Future _future) {
        closeableImpl = new CloseableImpl1(this);
        future = _future;
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }

    @Override
    public void close() throws Exception {
        future.cancel(false);
        closeableImpl.close();
    }
}
