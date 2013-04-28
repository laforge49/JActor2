package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface PADouble extends IncDes {

    public static final String FACTORY_NAME = "double";

    Request<Double> getDoubleReq();

    Double getValue();

    Request<Void> setDoubleReq(final Double _v);

    void setValue(final Double _v);
}
