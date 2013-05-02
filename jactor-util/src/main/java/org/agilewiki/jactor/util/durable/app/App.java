package org.agilewiki.jactor.util.durable.app;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.PASerializable;

public interface App extends PASerializable, Actor, Ancestor {
    void setDurable(final Durable _durable);
}
