package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Request;

public interface PAMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends PASerializable>
        extends PAList<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    public final static String STRING_PASTRING_BMAP = "stringStringBMap";
    public final static String STRING_BYTES_BMAP = "stringBytesBMap";
    public final static String STRING_BOX_BMAP = "stringBoxBMap";
    public final static String STRING_PALONG_BMAP = "stringLongBMap";
    public final static String STRING_PAINTEGER_BMAP = "stringIntBMap";
    public final static String STRING_PAFLOAT_BMAP = "stringFloatBMap";
    public final static String STRING_PADOUBLE_BMAP = "stringDoubleBMap";
    public final static String STRING_PABOOLEAN_BMAP = "stringBoolBMap";
    public final static String INTEGER_PASTRING_BMAP = "intStringBMap";
    public final static String INTEGER_PABYTES_BMAP = "intBytesBMap";
    public final static String INTEGER_BOX_BMAP = "intBoxBMap";
    public final static String INTEGER_PALONG_BMAP = "intLongBMap";
    public final static String INTEGER_PAINTEGER_BMAP = "intIntBMap";
    public final static String INTEGER_PAFLOAT_BMAP = "intFloatBMap";
    public final static String INTEGER_PADOUBLE_BMAP = "intDoubleBMap";
    public final static String INTEGER_PABOOLEAN_BMAP = "intBoolBMap";
    public final static String LONG_PASTRING_BMAP = "longStringBMap";
    public final static String LONG_BYTES_BMAP = "longBytesBMap";
    public final static String LONG_BOX_BMAP = "longBoxBMap";
    public final static String LONG_PALONG_BMAP = "longLongBMap";
    public final static String LONG_PAINTEGER_BMAP = "longIntBMap";
    public final static String LONG_PAFLOAT_BMAP = "longFloatBMap";
    public final static String LONG_PADOUBLE_BMAP = "longDoubleBMap";
    public final static String LONG_PABOOLEAN_BMAP = "longBoolBMap";

    public final static String STRING_PASTRING_MAP = "stringStringMap";
    public final static String STRING_BYTES_MAP = "stringBytesMap";
    public final static String STRING_BOX_MAP = "stringBoxMap";
    public final static String STRING_PALONG_MAP = "stringLongMap";
    public final static String STRING_PAINTEGER_MAP = "stringIntMap";
    public final static String STRING_PAFLOAT_MAP = "stringFloatMap";
    public final static String STRING_PADOUBLE_MAP = "stringDoubleMap";
    public final static String STRING_PABOOLEAN_MAP = "stringBoolMap";
    public final static String INTEGER_PASTRING_MAP = "intStringMap";
    public final static String INTEGER_BYTES_MAP = "intBytesMap";
    public final static String INTEGER_BOX_MAP = "intBoxMap";
    public final static String INTEGER_PALONG_MAP = "intLongMap";
    public final static String INTEGER_PAINTEGER_MAP = "intIntMap";
    public final static String INTEGER_PAFLOAT_MAP = "intFloatMap";
    public final static String INTEGER_PADOUBLE_MAP = "intDoubleMap";
    public final static String INTEGER_PABOOLEAN_MAP = "intBoolMap";
    public final static String LONG_PASTRING_MAP = "longStringMap";
    public final static String LONG_BYTES_MAP = "longBytesMap";
    public final static String LONG_BOX_MAP = "longBoxMap";
    public final static String LONG_PALONG_MAP = "longLongMap";
    public final static String LONG_PAINTEGER_MAP = "longIntMap";
    public final static String LONG_PAFLOAT_MAP = "longFloatMap";
    public final static String LONG_PADOUBLE_MAP = "longDoubleMap";
    public final static String LONG_PABOOLEAN_MAP = "longBoolMap";

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq();

    MapEntry<KEY_TYPE, VALUE_TYPE> getFirst();

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq();

    MapEntry<KEY_TYPE, VALUE_TYPE> getLast();

    Request<VALUE_TYPE> kGetReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value associated with the key.
     *
     * @param _key The key.
     * @return The actor assigned to the key, or null.
     */
    VALUE_TYPE kGet(final KEY_TYPE _key);

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value with a greater key.
     *
     * @param _key The key.
     * @return The matching jid, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getHigher(final KEY_TYPE _key);

    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(final KEY_TYPE _key);

    /**
     * Returns the Actor value with the smallest key >= the given key.
     *
     * @param _key The key.
     * @return The matching jid, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getCeiling(final KEY_TYPE _key);

    Request<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes);

    void kSet(final KEY_TYPE _key, final byte[] _bytes);

    Request<Boolean> kMakeReq(final KEY_TYPE _key);

    /**
     * Add a tuple to the map unless there is a tuple with a matching first element.
     *
     * @param _key Used to match the first element of the tuples.
     * @return True if a new tuple was created.
     */
    Boolean kMake(final KEY_TYPE _key);

    Request<Boolean> kMakeReq(final KEY_TYPE _key, final byte[] _bytes);

    /**
     * Add a tuple to the map unless there is a tuple with a matching first element.
     *
     * @param _key   Used to match the first element of the tuples.
     * @param _bytes The serialized form of a JID of the appropriate type.
     * @return True if a new tuple was created; otherwise the old value is unaltered.
     */
    Boolean kMake(final KEY_TYPE _key, final byte[] _bytes);

    Request<Boolean> kRemoveReq(final KEY_TYPE _key);

    /**
     * Removes the item identified by the key.
     *
     * @param _key The key.
     * @return True when the item was present and removed.
     */
    boolean kRemove(final KEY_TYPE _key);
}
