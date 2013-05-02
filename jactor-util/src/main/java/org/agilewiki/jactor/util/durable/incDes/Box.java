package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.pactor.api.Request;
import org.agilewiki.jactor.util.durable.PASerializable;

public interface Box extends IncDes {

    public static final String FACTORY_NAME = "box";

    Request<PASerializable> getValueReq();

    PASerializable getValue()
            throws Exception;

    Request<Void> clearReq();

    void clear();

    Request<Void> setValueReq(final String _v);

    void setValue(final String _jidType)
            throws Exception;

    Request<Void> setValueReq(final String _v, final byte[] _bytes);

    void setValue(final String _jidType, final byte[] _bytes)
            throws Exception;

    Request<Boolean> makeValueReq(final String _v);

    Boolean makeValue(final String _jidType)
            throws Exception;

    Request<Boolean> makeValueReq(final String _v, final byte[] _bytes);

    Boolean makeValue(final String _jidType, final byte[] _bytes)
            throws Exception;
}
