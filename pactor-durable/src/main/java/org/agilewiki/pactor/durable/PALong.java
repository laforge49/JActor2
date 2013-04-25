package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PALong extends IncDes {

    public static final String FACTORY_NAME = "long";

    Request<Long> getLongReq();

    Long getValue();

    Request<Void> setLongReq(final Long _v);

    void setValue(final Long _v) throws Exception;
}
