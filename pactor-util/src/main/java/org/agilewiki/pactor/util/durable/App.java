package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.pactor.util.Ancestor;

public interface App extends PASerializable, Actor, Ancestor {
    void setDurable(final Durable _durable);
}
