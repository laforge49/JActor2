package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.util.durable.JASerializable;

/**
 * A serializable list.
 *
 * @param <ENTRY_TYPE> The type of list entry.
 */
public interface JAList<ENTRY_TYPE extends JASerializable> extends
        Collection<ENTRY_TYPE> {

    /**
     * Factory name for a list of JAString.
     */
    public static final String JASTRING_LIST = "stringList";

    /**
     * Factory name for a list of Bytes.
     */
    public static final String BYTES_LIST = "bytesList";

    /**
     * Factory name for a list of Box.
     */
    public static final String BOX_LIST = "boxList";

    /**
     * Factory name for a list of JALong.
     */
    public static final String JALONG_LIST = "longList";

    /**
     * Factory name for a list of JAInteger.
     */
    public static final String JAINTEGER_LIST = "intList";

    /**
     * Factory name for a list of JAFloat.
     */
    public static final String JAFLOAT_LIST = "floatList";

    /**
     * Factory name for a list of JADouble.
     */
    public static final String JADOUBLE_LIST = "doubleList";

    /**
     * Factory name for a list of JABoolean.
     */
    public static final String JABOOLEAN_LIST = "boolList";

    /**
     * Returns a request to empty the list.
     *
     * @return The request.
     */
    AsyncRequest<Void> emptyReq();

    /**
     * Empties the list.
     */
    void empty() throws Exception;

    /**
     * Returns a request to insert a new serializable object into the list.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     * @return The request.
     */
    AsyncRequest<Void> iAddReq(final int _i);

    /**
     * Insert a new serializable object into the list.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     */
    void iAdd(final int _i) throws Exception;

    /**
     * Returns a request to insert and initialize a new serializable object into the list.
     *
     * @param _i     An index, where 0 is the first element and -1 is the last element.
     * @param _bytes The serialized data used to initialize the new serializable object.
     * @return The request.
     */
    AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes);

    /**
     * Insert and initialize a new serializable object into the list.
     *
     * @param _i     An index, where 0 is the first element and -1 is the last element.
     * @param _bytes The serialized data used to initialize the new serializable object.
     */
    void iAdd(final int _i, final byte[] _bytes) throws Exception;

    /**
     * Returns a request to remove an object from the list.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     * @return The request.
     */
    AsyncRequest<Void> iRemoveReq(final int _i);

    /**
     * Removes an object from the list.
     *
     * @param _i An index, where 0 is the first element and -1 is the last element.
     */
    void iRemove(final int _i) throws Exception;
}
