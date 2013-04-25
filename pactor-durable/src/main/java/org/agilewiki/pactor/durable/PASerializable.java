package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Actor;
import org.agilewiki.pautil.Ancestor;

public interface PASerializable extends Actor, Ancestor {
    IncDes getDurable();
}
