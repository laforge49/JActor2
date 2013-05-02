package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface PABoolean extends IncDes {

    /**
     * Size of a boolean in bytes.
     */
    public final static int LENGTH = 1;

    public static final String FACTORY_NAME = "bool";

    Request<Boolean> getValueReq();

    Boolean getValue();

    Request<Void> setValueReq(final Boolean _v);

    void setValue(final Boolean _v);
}
