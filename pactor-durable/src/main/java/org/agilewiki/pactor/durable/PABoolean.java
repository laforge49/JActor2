package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PABoolean extends IncDes {
    Request<Boolean> getBooleanReq();

    Boolean getValue();

    Request<Void> setBooleanReq(final Boolean _v);

    void setValue(final Boolean _v) throws Exception;
}
