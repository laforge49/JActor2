package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface PALong extends IncDes {

    /**
     * Size of a long in bytes.
     */
    public final static int LENGTH = 8;

    public static final String FACTORY_NAME = "long";

    Request<Long> getValueReq();

    Long getValue();

    Request<Void> setValueReq(final Long _v);

    void setValue(final Long _v);
}
