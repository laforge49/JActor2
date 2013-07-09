package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.api.Request;
import org.agilewiki.jactor2.util.durable.JASerializable;

/**
 * A serializable map.
 *
 * @param <KEY_TYPE>   The type of key.
 * @param <VALUE_TYPE> The type of value.
 */
public interface JAMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends JASerializable>
        extends JAList<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    /**
     * Factory name for a JAMap&lt;String, JAString&gt;.
     */
    public final static String STRING_JASTRING_MAP = "stringStringMap";

    /**
     * Factory name for a JAMap&lt;String, Bytes&gt;.
     */
    public final static String STRING_BYTES_MAP = "stringBytesMap";

    /**
     * Factory name for a JAMap&lt;String, Box&gt;.
     */
    public final static String STRING_BOX_MAP = "stringBoxMap";

    /**
     * Factory name for a JAMap&lt;String, JALong&gt;.
     */
    public final static String STRING_JALONG_MAP = "stringLongMap";

    /**
     * Factory name for a JAMap&lt;String, JAInteger&gt;.
     */
    public final static String STRING_JAINTEGER_MAP = "stringIntMap";

    /**
     * Factory name for a JAMap&lt;String, JAFloat&gt;.
     */
    public final static String STRING_JAFLOAT_MAP = "stringFloatMap";

    /**
     * Factory name for a JAMap&lt;String, JADouble&gt;.
     */
    public final static String STRING_JADOUBLE_MAP = "stringDoubleMap";

    /**
     * Factory name for a JAMap&lt;String, JABoolean&gt;.
     */
    public final static String STRING_JABOOLEAN_MAP = "stringBoolMap";

    /**
     * Factory name for a JAMap&lt;Integer, JAString&gt;.
     */
    public final static String INTEGER_JASTRING_MAP = "intStringMap";

    /**
     * Factory name for a JAMap&lt;Integer, Bytes&gt;.
     */
    public final static String INTEGER_BYTES_MAP = "intBytesMap";

    /**
     * Factory name for a JAMap&lt;Integer, Box&gt;.
     */
    public final static String INTEGER_BOX_MAP = "intBoxMap";

    /**
     * Factory name for a JAMap&lt;Integer, JALong&gt;.
     */
    public final static String INTEGER_JALONG_MAP = "intLongMap";

    /**
     * Factory name for a JAMap&lt;Integer, JAInteger&gt;.
     */
    public final static String INTEGER_JAINTEGER_MAP = "intIntMap";

    /**
     * Factory name for a JAMap&lt;Integer, JAFloat&gt;.
     */
    public final static String INTEGER_JAFLOAT_MAP = "intFloatMap";

    /**
     * Factory name for a JAMap&lt;Integer, JADouble&gt;.
     */
    public final static String INTEGER_JADOUBLE_MAP = "intDoubleMap";

    /**
     * Factory name for a JAMap&lt;Integer, JABoolean&gt;.
     */
    public final static String INTEGER_JABOOLEAN_MAP = "intBoolMap";

    /**
     * Factory name for a JAMap&lt;Long, JAString&gt;.
     */
    public final static String LONG_JASTRING_MAP = "longStringMap";

    /**
     * Factory name for a JAMap&lt;Long, Bytes&gt;.
     */
    public final static String LONG_BYTES_MAP = "longBytesMap";

    /**
     * Factory name for a JAMap&lt;Long, Box&gt;.
     */
    public final static String LONG_BOX_MAP = "longBoxMap";

    /**
     * Factory name for a JAMap&lt;Long, JALong&gt;.
     */
    public final static String LONG_JALONG_MAP = "longLongMap";

    /**
     * Factory name for a JAMap&lt;Long, JAInteger&gt;.
     */
    public final static String LONG_JAINTEGER_MAP = "longIntMap";

    /**
     * Factory name for a JAMap&lt;Long, JAFloat&gt;.
     */
    public final static String LONG_JAFLOAT_MAP = "longFloatMap";

    /**
     * Factory name for a JAMap&lt;Long, JADouble&gt;.
     */
    public final static String LONG_JADOUBLE_MAP = "longDoubleMap";

    /**
     * Factory name for a JAMap&lt;Long, JABoolean&gt;.
     */
    public final static String LONG_JABOOLEAN_MAP = "longBoolMap";

    /**
     * Returns a request to get the first entry.
     *
     * @return The request.
     */
    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq();

    /**
     * Returns the first entry.
     *
     * @return The first entry, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getFirst()
            throws Exception;

    /**
     * Returns a request to get the last entry.
     *
     * @return The request.
     */
    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq();

    /**
     * Returns the last entry.
     *
     * @return The last entry, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getLast()
            throws Exception;

    /**
     * Returns a request to get the serializable object assigned to a given key.
     *
     * @param _key The key.
     * @return The request.
     */
    Request<VALUE_TYPE> kGetReq(final KEY_TYPE _key);

    /**
     * Returns the serializable object assigned to a given key.
     *
     * @param _key The key.
     * @return The assigned serializable object, or null.
     */
    VALUE_TYPE kGet(final KEY_TYPE _key)
            throws Exception;

    /**
     * Returns a request to get an entry with a higher key.
     *
     * @param _key The key.
     * @return The request.
     */
    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(final KEY_TYPE _key);

    /**
     * Returns the entry with a higher key.
     *
     * @param _key The key.
     * @return An entry, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getHigher(final KEY_TYPE _key)
            throws Exception;

    /**
     * Returns a request to get an entry with a key greater or equal to the given key.
     *
     * @param _key The key.
     * @return The request.
     */
    Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(final KEY_TYPE _key);

    /**
     * Returns an entry with a key greater or equal to the given key.
     *
     * @param _key The key.
     * @return The entry, or null.
     */
    MapEntry<KEY_TYPE, VALUE_TYPE> getCeiling(final KEY_TYPE _key)
            throws Exception;

    /**
     * Returns a request to update an existing serializable object.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key   The key of the serializable object.
     * @param _bytes The serialized data used to update the object.
     * @return The request.
     */
    Request<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes);

    /**
     * Updates an existing serializable object.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key   The key of the serializable object.
     * @param _bytes The serialized data used to update the object.
     */
    void kSet(final KEY_TYPE _key, final byte[] _bytes)
            throws Exception;

    /**
     * Returns a request to create a new serializable object and add it to the map, unless there is
     * already an entry with the same key.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key The key.
     * @return The request.
     */
    Request<Boolean> kMakeReq(final KEY_TYPE _key);

    /**
     * Create a new serializable object and add it to the map, unless there is
     * already an entry with the same key.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key The key.
     * @return True if a new entry was added.
     */
    Boolean kMake(final KEY_TYPE _key)
            throws Exception;

    /**
     * Returns a request to create a new serializable object and add it to the map, unless there is
     * already an entry with the same key.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key   The key.
     * @param _bytes The serialized data used to initialize the new object.
     * @return The request.
     */
    Request<Boolean> kMakeReq(final KEY_TYPE _key, final byte[] _bytes);

    /**
     * Create a new serializable object and add it to the map, unless there is
     * already an entry with the same key.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _key   The key.
     * @param _bytes The serialized data used to initialize the new object.
     * @return True if a new entry was added.
     */
    Boolean kMake(final KEY_TYPE _key, final byte[] _bytes)
            throws Exception;

    /**
     * Returns a request to remove an entry with a given key.
     *
     * @param _key The key.
     * @return The request.
     */
    Request<Boolean> kRemoveReq(final KEY_TYPE _key);

    /**
     * Removes the entry with the given key.
     *
     * @param _key The key.
     * @return True when an entry was present and removed.
     */
    boolean kRemove(final KEY_TYPE _key)
            throws Exception;
}
