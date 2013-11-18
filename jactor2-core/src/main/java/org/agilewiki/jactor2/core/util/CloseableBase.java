package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CloseableBase extends BladeBase implements Closeable {
    private Set<Closer> closers = new HashSet<Closer>();

    @Override
    public SyncRequest<Void> addCloserSReq(final Closer _closer) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                closers.add(_closer);
                return null;
            }
        };
    }

    @Override
    public SyncRequest<Void> removeCloserSReq(final Closer _closer) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                closers.remove(_closer);
                return null;
            }
        };
    }

    @Override
    public void close() throws Exception {
        Iterator<Closer> it = closers.iterator();
        while (it.hasNext()) {
            Closer closer = it.next();
            closer.removeCloseableSReq(this).signal();
        }
    }
}
