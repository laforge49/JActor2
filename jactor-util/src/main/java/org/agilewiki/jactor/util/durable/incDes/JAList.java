package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.util.durable.JASerializable;

public interface JAList<ENTRY_TYPE extends JASerializable> extends Collection<ENTRY_TYPE> {

    public final static String JASTRING_LIST = "stringList";
    public final static String BYTES_LIST = "bytesList";
    public final static String BOX_LIST = "boxList";
    public final static String JALONG_LIST = "longList";
    public final static String JAINTEGER_LIST = "intList";
    public final static String JAFLOAT_LIST = "floatList";
    public final static String JADOUBLE_LIST = "doubleList";
    public final static String JABOOLEAN_LIST = "boolList";

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
