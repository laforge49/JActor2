package org.agilewiki.jactor2.core.closeable;

import org.agilewiki.jactor2.core.impl.ReactorImpl;

public interface CloseableImpl extends AutoCloseable {
    public void addReactor(final ReactorImpl _reactorImpl) throws Exception;
    public void removeReactor(final ReactorImpl _reactorImpl);
}
