package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.api.Request;

public interface PAFloat extends IncDes {

    public static final String FACTORY_NAME = "float";

    Request<Float> getFloatReq();

    Float getValue();

    Request<Void> setFloatReq(final Float _v);

    void setValue(final Float _v) throws Exception;
}
