package org.agilewiki.jactor2.util.durable.app;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;

/**
 * An optional base class for serializable application classes.
 */
public class AppBase implements App {

    /**
     * The durable part of the serializable object.
     */
    private Durable durable;

    @Override
    public void setDurable(final Durable _durable) {
        if (durable != null)
            throw new IllegalStateException("durable already set");
        durable = _durable;
    }

    @Override
    public Durable getDurable() {
        return durable;
    }

    @Override
    public Reactor getReactor() {
        return durable.getReactor();
    }

    @Override
    public Ancestor getParent() {
        return durable.getParent();
    }
}
