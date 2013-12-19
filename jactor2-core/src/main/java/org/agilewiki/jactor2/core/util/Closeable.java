package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.Blade;

public interface Closeable extends AutoCloseable, Blade {
    void addCloser(final Closer _closer) throws Exception;
    void removeCloser(final Closer _closer);
}
