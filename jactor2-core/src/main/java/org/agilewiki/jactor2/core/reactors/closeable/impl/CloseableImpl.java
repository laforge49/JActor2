package org.agilewiki.jactor2.core.reactors.closeable.impl;

import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * The interface of the code that actually implements closeable.
 */
public interface CloseableImpl extends AutoCloseable {
    /**
     * Called by a reactorImpl to add itself to the closeable's set of dependencies.
     *
     * @param _reactorImpl    The new dependency.
     */
    public void addReactor(final ReactorImpl _reactorImpl);

    /**
     * Called by a reactorImpl to remove itself from the closeable's set of dependencies.
     *
     * @param _reactorImpl    The old dependency.
     */
    public void removeReactor(final ReactorImpl _reactorImpl);
}
