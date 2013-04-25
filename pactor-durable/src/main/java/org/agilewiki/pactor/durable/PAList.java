package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PAList<ENTRY_TYPE extends PASerializable> extends Collection<ENTRY_TYPE> {

    Request<Void> emptyReq();

    void empty()
            throws Exception;

    Request<Void> iAddReq(final int _i);

    void iAdd(final int _i)
            throws Exception;

    Request<Void> iAddReq(final int _i, final byte[] _bytes);

    void iAdd(final int _i, final byte[] _bytes)
            throws Exception;

    Request<Void> iRemoveReq(final int _i);

    void iRemove(final int _i)
            throws Exception;
}
