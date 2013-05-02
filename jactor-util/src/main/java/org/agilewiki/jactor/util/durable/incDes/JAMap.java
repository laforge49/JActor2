package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.util.durable.JASerializable;

public interface JAMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends JASerializable>
        extends JAList<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    public final static String STRING_JASTRING_MAP = "stringStringMap";
    public final static String STRING_BYTES_MAP = "stringBytesMap";
    public final static String STRING_BOX_MAP = "stringBoxMap";
    public final static String STRING_JALONG_MAP = "stringLongMap";
    public final static String STRING_JAINTEGER_MAP = "stringIntMap";
    public final static String STRING_JAFLOAT_MAP = "stringFloatMap";
    public final static String STRING_JADOUBLE_MAP = "stringDoubleMap";
    public final static String STRING_JABOOLEAN_MAP = "stringBoolMap";

    public final static String INTEGER_JASTRING_MAP = "intStringMap";
    public final static String INTEGER_BYTES_MAP = "intBytesMap";
    public final static String INTEGER_BOX_MAP = "intBoxMap";
    public final static String INTEGER_JALONG_MAP = "intLongMap";
    public final static String INTEGER_JAINTEGER_MAP = "intIntMap";
    public final static String INTEGER_JAFLOAT_MAP = "intFloatMap";
    public final static String INTEGER_JADOUBLE_MAP = "intDoubleMap";
    public final static String INTEGER_JABOOLEAN_MAP = "intBoolMap";

    public final static String LONG_JASTRING_MAP = "longStringMap";
    public final static String LONG_BYTES_MAP = "longBytesMap";
    public final static String LONG_BOX_MAP = "longBoxMap";
    public final static String LONG_JALONG_MAP = "longLongMap";
    public final static String LONG_JAINTEGER_MAP = "longIntMap";
    public final static String LONG_JAFLOAT_MAP = "longFloatMap";
    public final static String LONG_JADOUBLE_MAP = "longDoubleMap";
    public final static String LONG_JABOOLEAN_MAP = "longBoolMap";

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq();

    MapEntry<KEY_TYPE, VALUE_TYPE> getFirst()
            throws Exception;

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq();

    MapEntry<KEY_TYPE, VALUE_TYPE> getLast()
            throws Exception;

    Request<VALUE_TYPE> kGetReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value associated with the key.
     *
     * @param _key The key.
     * @return The actor assigned to the key, or null.
     */
    VALUE_TYPE kGet(final KEY_TYPE _key)
            throws Exception;

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value with a greater key.
     *
     * @param _key The key.
     * @return The matching jid, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getHigher(final KEY_TYPE _key)
            throws Exception;

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value with the smallest key >= the given key.
     *
     * @param _key The key.
     * @return The matching jid, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getCeiling(final KEY_TYPE _key)
            throws Exception;

    Request<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes);

    void kSet(final KEY_TYPE _key, final byte[] _bytes)
            throws Exception;

    Request<Boolean> kMakeReq(final KEY_TYPE _key);

    /**
     * Add a tuple to the map unless there is a tuple with a matching first element.
     *
     * @param _key Used to match the first element of the tuples.
     * @return True if a new tuple was created.
     */
    Boolean kMake(final KEY_TYPE _key)
            throws Exception;

    Request<Boolean> kMakeReq(final KEY_TYPE _key, final byte[] _bytes);

    /**
     * Add a tuple to the map unless there is a tuple with a matching first element.
     *
     * @param _key   Used to match the first element of the tuples.
     * @param _bytes The serialized form of a JID of the appropriate type.
     * @return True if a new tuple was created; otherwise the old value is unaltered.
     */
    Boolean kMake(final KEY_TYPE _key, final byte[] _bytes)
            throws Exception;

    Request<Boolean> kRemoveReq(final KEY_TYPE _key);

    /**
     * Removes the item identified by the key.
     *
     * @param _key The key.
     * @return True when the item was present and removed.
     */
    boolean kRemove(final KEY_TYPE _key)
            throws Exception;
}
