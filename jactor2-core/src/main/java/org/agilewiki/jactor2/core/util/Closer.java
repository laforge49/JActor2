package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.Blade;

public interface Closer extends Blade, AutoCloseable {

    boolean addCloseable(final Closeable _closeable) throws Exception;
    boolean removeCloseable(final Closeable _closeable);
}
