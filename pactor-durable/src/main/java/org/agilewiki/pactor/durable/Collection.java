package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface Collection<ENTRY_TYPE extends PASerializable> extends IncDes {

    Request<Integer> sizeReq();

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    int size()
            throws Exception;

    Request<ENTRY_TYPE> iGetReq(final int _i);

    /**
     * Returns the selected element.
     *
     * @param _ndx Selects the element.
     * @return The ith JID component, or null if the index is out of range.
     */
    ENTRY_TYPE iGet(final int _ndx)
            throws Exception;

    Request<Void> iSetReq(final int _i, final byte[] _bytes);

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param _i     The index of the desired element.
     * @param _bytes Holds the serialized data.
     * @throws Exception Any exceptions thrown while processing the request.
     */
    void iSet(final int _i, final byte[] _bytes)
            throws Exception;
}
