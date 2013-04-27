package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.api.Request;

public interface Bytes extends IncDes {

    public static final String FACTORY_NAME = "bytes";

    Request<byte[]> getBytesReq();

    byte[] getValue();

    Object getObject() throws Exception;

    Request<Void> clearReq();

    void clear() throws Exception;

    Request<Void> setBytesReq(final byte[] _v);

    void setValue(final byte[] _v) throws Exception;

    void setObject(Object v) throws Exception;

    Request<Boolean> makeBytesReq(final byte[] _v);

    Boolean makeValue(final byte[] v) throws Exception;
}
