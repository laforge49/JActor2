package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface PAInteger extends IncDes {

    public static final String FACTORY_NAME = "int";

    Request<Integer> getValueReq();

    Integer getValue();

    Request<Void> setValueReq(final Integer _v);

    void setValue(final Integer _v);
}
