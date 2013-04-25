package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Request;

public interface PAMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends PASerializable>
        extends PAList<MapEntry<KEY_TYPE, VALUE_TYPE>> {

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
