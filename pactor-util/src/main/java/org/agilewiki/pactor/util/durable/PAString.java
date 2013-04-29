package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface PAString extends IncDes {

    public static final String FACTORY_NAME = "string";

    Request<String> getValueReq();

    String getValue();

    Request<Void> clearReq();

    void clear();

    Request<Void> setValueReq(final String _v);

    void setValue(final String _v);

    Request<Boolean> makeValueReq(final String _v);

    Boolean makeValue(final String _v);
}
