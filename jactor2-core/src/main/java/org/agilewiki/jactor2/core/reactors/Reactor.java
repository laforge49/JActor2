package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * The Reactor interface identifies the processing methods that can be used by applications.
 */
public interface Reactor extends AutoCloseable, Blade {

    ReactorImpl asReactorImpl();

    /**
     * Returns true when there are no more messages in the inbox. This method is generally
     * only called by a processing's onIdle task to determine when to return so that an
     * incoming message can be processed.
     *
     * @return True when the inbox is empty.
     */
    boolean isInboxEmpty();

    SyncRequest<Void> nullSReq();

    Reactor getParentReactor();

    boolean addCloseable(final Closeable _closeable) throws Exception;
    boolean removeCloseable(final Closeable _closeable);
    Recovery getRecovery();
    void setRecovery(final Recovery _recovery);
}
