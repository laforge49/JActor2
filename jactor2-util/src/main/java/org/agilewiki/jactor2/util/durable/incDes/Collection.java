package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.util.durable.JASerializable;

public interface Collection<ENTRY_TYPE extends JASerializable> extends IncDes {

    /**
     * Returns a request that gets the size.
     *
     * @return The request.
     */
    Request<Integer> sizeReq();

    /**
     * Returns the size.
     *
     * @return The size.
     */
    int size()
            throws Exception;

    /**
     * Returns a request that gets the ith element.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     * @return The request.
     */
    Request<ENTRY_TYPE> iGetReq(final int _i);

    /**
     * Returns the ith element.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     * @return The ith element, or null if the index is out of range.
     */
    ENTRY_TYPE iGet(final int _i)
            throws Exception;

    /**
     * Returns a request that updates an existing serializable object.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _i     An index, where 0 is the first element and -1 is the last element.
     * @param _bytes The new content.
     * @return The request.
     */
    Request<Void> iSetReq(final int _i, final byte[] _bytes);

    /**
     * Updates an existing serializable object.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _i     An index, where 0 is the first element and -1 is the last element.
     * @param _bytes The new content.
     */
    void iSet(final int _i, final byte[] _bytes)
            throws Exception;
}
