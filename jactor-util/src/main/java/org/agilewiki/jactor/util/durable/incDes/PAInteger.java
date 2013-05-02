package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface PAInteger extends IncDes {

    /**
     * Size of an int in bytes.
     */
    public final static int LENGTH = 4;

    public static final String FACTORY_NAME = "int";

    Request<Integer> getValueReq();

    Integer getValue();

    Request<Void> setValueReq(final Integer _v);

    void setValue(final Integer _v);
}
