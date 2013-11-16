package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closer {

    /**
     * Returns a request to add an auto closeable, to be closed when the Facility closes.
     * This request returns true if the AutoClosable was added.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return The request.
     */
    SyncRequest<Boolean> addAutoClosableSReq(final AutoCloseable _closeable);

    /**
     * Returns a request to remove an auto closeable.
     * This request returns true if the AutoClosable was removed.
     *
     * @param _closeable The autoclosable to be removed.
     * @return The request.
     */
    SyncRequest<Boolean> removeAutoClosableSReq(final AutoCloseable _closeable);
}
