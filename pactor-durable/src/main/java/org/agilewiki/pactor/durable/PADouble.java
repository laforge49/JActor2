package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PADouble extends IncDes {
    Request<Double> getDoubleReq();

    Double getValue();

    Request<Void> setDoubleReq(final Double _v);

    void setValue(final Double _v) throws Exception;
}
