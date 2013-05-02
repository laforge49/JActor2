package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface JAFloat extends IncDes {

    /**
     * Size of an float in bytes.
     */
    public final static int LENGTH = 4;

    public static final String FACTORY_NAME = "float";

    Request<Float> getValueReq();

    Float getValue();

    Request<Void> setValueReq(final Float _v);

    void setValue(final Float _v);
}
