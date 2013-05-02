package org.agilewiki.jactor.util.durable.app;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.JASerializable;

public interface App extends JASerializable, Actor, Ancestor {
    void setDurable(final Durable _durable);
}
