package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface Closeable extends AutoCloseable, Blade {
    SyncRequest<Void> addCloserSReq(final Closer _closer);
    SyncRequest<Void> removeCloserSReq(final Closer _closer);
}
