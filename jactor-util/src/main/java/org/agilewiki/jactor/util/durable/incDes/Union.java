package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.util.durable.JASerializable;

public interface Union extends IncDes {

    Request<JASerializable> getValueReq();

    JASerializable getValue();

    Request<Void> clearReq();

    void clear()
            throws Exception;

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
