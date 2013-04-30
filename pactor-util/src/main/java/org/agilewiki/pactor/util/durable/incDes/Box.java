package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.util.durable.PASerializable;

public interface Box extends IncDes {

    public static final String FACTORY_NAME = "box";

    Request<PASerializable> getValueReq();

    PASerializable getValue();

    Request<Void> clearReq();

    void clear();

    Request<Void> setValueReq(final String _v);

    void setValue(final String _jidType);

    Request<Void> setValueReq(final String _v, final byte[] _bytes);

    void setValue(final String _jidType, final byte[] _bytes);

    Request<Boolean> makeValueReq(final String _v);

    Boolean makeValue(final String _jidType);

    Request<Boolean> makeValueReq(final String _v, final byte[] _bytes);

    Boolean makeValue(final String _jidType, final byte[] _bytes);
}
