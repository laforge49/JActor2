package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PAInteger extends IncDes {
    Request<Integer> getIntegerReq();

    Integer getValue();

    Request<Void> setIntegerReq(final Integer _v);

    void setValue(final Integer _v) throws Exception;
}
