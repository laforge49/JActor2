package org.agilewiki.jactor2.core.reactors;

        import org.agilewiki.jactor2.core.impl.CloseableImpl;

public interface Closeable extends AutoCloseable {
    CloseableImpl asCloseableImpl();
}
