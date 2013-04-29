package org.agilewiki.pactor.util.durable.app;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.PASerializable;

public interface App extends PASerializable, Actor, Ancestor {
    void setDurable(final Durable _durable);
}
