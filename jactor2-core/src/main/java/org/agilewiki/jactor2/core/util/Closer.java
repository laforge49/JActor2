package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closer {

    boolean addCloseable(final Closeable _closeable) throws ServiceClosedException;
    SyncRequest<Boolean> removeCloseableSReq(final Closeable _closeable);
}
