package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closeable extends AutoCloseable {
    SyncRequest<Boolean> addCloserSReq(final Closer _closer);
    SyncRequest<Boolean> removeCloserSReq(final Closer _closer);
}
