package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface Box extends IncDes {

    public static final String FACTORY_NAME = "box";

    Request<PASerializable> getIncDesReq();

    PASerializable getValue();

    Request<Void> clearReq();

    void clear();

    Request<Void> setIncDesReq(final String _v);

    void setValue(final String _jidType);

    Request<Void> setIncDesReq(final String _v, final byte[] _bytes);

    void setValue(final String _jidType, final byte[] _bytes);

    Request<Boolean> makeIncDesReq(final String _v);

    Boolean makeValue(final String _jidType);

    Request<Boolean> makeIncDesReq(final String _v, final byte[] _bytes);

    Boolean makeValue(final String _jidType, final byte[] _bytes);
}
