package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.pactor.util.Ancestor;

public interface PASerializable extends Actor, Ancestor {
    IncDes getDurable();
}
