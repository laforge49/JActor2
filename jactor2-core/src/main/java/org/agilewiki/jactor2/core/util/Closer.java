package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closer {

    boolean addCloseable(final Closeable _closeable) throws ServiceClosedException;
    boolean removeCloseable(final Closeable _closeable);
}
