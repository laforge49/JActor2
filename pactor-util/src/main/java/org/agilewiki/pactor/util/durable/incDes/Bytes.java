package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.api.Request;

public interface Bytes extends IncDes {

    public static final String FACTORY_NAME = "bytes";

    Request<byte[]> getValueReq();

    byte[] getValue();

    Object getObject() throws Exception;

    Request<Void> clearReq();

    void clear();

    Request<Void> setValueReq(final byte[] _v);

    void setValue(final byte[] _v);

    void setObject(Object v) throws Exception;

    Request<Boolean> makeValueReq(final byte[] _v);

    Boolean makeValue(final byte[] v);
}
