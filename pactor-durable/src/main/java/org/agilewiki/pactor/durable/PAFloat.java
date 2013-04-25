package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PAFloat extends IncDes {
    Request<Float> getFloatReq();

    Float getValue();

    Request<Void> setFloatReq(final Float _v);

    void setValue(final Float _v) throws Exception;
}
