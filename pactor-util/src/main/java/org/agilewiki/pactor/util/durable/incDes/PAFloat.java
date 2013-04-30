package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.api.Request;

public interface PAFloat extends IncDes {

    public static final String FACTORY_NAME = "float";

    Request<Float> getValueReq();

    Float getValue();

    Request<Void> setValueReq(final Float _v);

    void setValue(final Float _v);
}
