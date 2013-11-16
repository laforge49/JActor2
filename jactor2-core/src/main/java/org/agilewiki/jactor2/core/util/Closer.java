package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closer {

    /**
     * Returns a request to add a closeable, to be closed when the Facility closes.
     * This request returns true if the  Closeable was added.
     *
     * @param _closeable The closeable to be added to the list.
     * @return The request.
     */
    SyncRequest<Boolean> addClosableSReq(final Closeable _closeable);

    /**
     * Returns a request to remove a closeable.
     * This request returns true if the Closeable was removed.
     *
     * @param _closeable The closeable to be removed.
     * @return The request.
     */
    SyncRequest<Boolean> removeClosableSReq(final Closeable _closeable);
}
