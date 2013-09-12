package org.agilewiki.jactor2.util.durable.app;

import org.agilewiki.jactor2.core.Blade;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.JASerializable;

/**
 * Implementing App is an easy way to make application classes serializable.
 */
public interface App extends JASerializable, Blade, Ancestor {

    /**
     * Sets the durable reference.
     *
     * @param _durable A tuple defined when registering the application factory.
     */
    void setDurable(final Durable _durable);
}
