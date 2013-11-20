package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closeable extends AutoCloseable, Blade {
    void addCloser(final Closer _closer) throws ServiceClosedException;
    void removeCloser(final Closer _closer);
}
