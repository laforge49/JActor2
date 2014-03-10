package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.closeable.CloseableImpl1;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Closes the request when the target reactor is closed,
 * all requests saved in a blade's state should be persistent requests.
 *
 * @param <RESPONSE_TYPE> The response type.
 */
public abstract class PersistentRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> implements Closeable {
    private CloseableImpl closeableImpl = new CloseableImpl1(this);

    /**
     * Create a persistent request.
     *
     * @param _targetReactor
     */
    public PersistentRequest(Reactor _targetReactor) {
        super(_targetReactor);
        _targetReactor.addCloseable(this);
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }

    @Override
    public void close() throws Exception {
        asRequestImpl().close();
    }
}
