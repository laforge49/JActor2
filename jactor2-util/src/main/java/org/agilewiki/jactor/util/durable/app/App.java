package org.agilewiki.jactor.util.durable.app;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.JASerializable;

/**
 * Implementing App is an easy way to make application classes serializable.
 */
public interface App extends JASerializable, Actor, Ancestor {

    /**
     * Sets the durable reference.
     *
     * @param _durable A tuple defined when registering the application factory.
     */
    void setDurable(final Durable _durable);
}
