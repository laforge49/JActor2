package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface JADouble extends IncDes {

    /**
     * Size of an double in bytes.
     */
    public final static int LENGTH = 8;

    public static final String FACTORY_NAME = "double";

    Request<Double> getValueReq();

    Double getValue();

    Request<Void> setValueReq(final Double _v);

    void setValue(final Double _v);
}
