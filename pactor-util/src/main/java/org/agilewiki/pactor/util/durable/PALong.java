package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface PALong extends IncDes {

    public static final String FACTORY_NAME = "long";

    Request<Long> getValueReq();

    Long getValue();

    Request<Void> setValueReq(final Long _v);

    void setValue(final Long _v);
}
