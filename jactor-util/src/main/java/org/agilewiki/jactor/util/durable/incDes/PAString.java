package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface PAString extends IncDes {

    public static final String FACTORY_NAME = "string";

    Request<String> getValueReq();

    String getValue()
            throws Exception;

    Request<Void> clearReq();

    void clear();

    Request<Void> setValueReq(final String _v);

    void setValue(final String _v);

    Request<Boolean> makeValueReq(final String _v);

    Boolean makeValue(final String _v);
}
