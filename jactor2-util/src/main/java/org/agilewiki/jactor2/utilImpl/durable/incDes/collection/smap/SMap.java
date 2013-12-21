package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.Collection;
import org.agilewiki.jactor2.util.durable.incDes.JAMap;
import org.agilewiki.jactor2.util.durable.incDes.MapEntry;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.MapEntryImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist.SList;

/**
 * Holds a map.
 */
abstract public class SMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends JASerializable>
        extends SList<MapEntry<KEY_TYPE, VALUE_TYPE>> implements
        JAMap<KEY_TYPE, VALUE_TYPE>, Collection<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    public Factory valueFactory;

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq() {
        return new AsyncBladeRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getFirst());
            }
        };
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq() {
        return new AsyncBladeRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getLast());
            }
        };
    }

    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    abstract protected Factory getKeyFactory() throws Exception;

    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    abstract protected KEY_TYPE stringToKey(String skey);

    /**
     * Returns the jid type of all the elements in the list.
     *
     * @return The jid type of all the elements in the list.
     */
    @Override
    final protected Factory getEntryFactory() throws Exception {
        final Factory af = Durables.getFactoryLocator(getReactor()).getFactory(
                "E." + getFactoryName());
        return af;
    }

    /**
     * Returns the IncDesFactory for the values in the map.
     *
     * @return The jid factory of the values in the list.
     */
    protected Factory getValueFactory() {
        if (valueFactory != null) {
            return valueFactory;
        }
        throw new IllegalStateException("valueFactory not set");
    }

    /**
     * Locate the entry with a matching first element.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or - (insertion point + 1).
     */
    public final int search(final KEY_TYPE key) throws Exception {
        initializeList();
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            final int mid = (low + high) >>> 1;
            final MapEntryImpl<KEY_TYPE, VALUE_TYPE> midVal = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(mid);
            final int c = midVal.compareKeyTo(key);
            if (c < 0) {
                low = mid + 1;
            } else if (c > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    /**
     * Locate the entry with a higher key.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or -1.
     */
    public final int higher(final KEY_TYPE key) throws Exception {
        int i = search(key);
        if (i > -1) {
            i += 1;
        } else {
            i = -i - 1;
        }
        if (i == size()) {
            return -1;
        }
        return i;
    }

    /**
     * Locate the entry with the first element &gt;= a key, or the last entry.
     *
     * @param key The key which matches to the entry's first element, or size.
     * @return The index, or size.
     */
    public final int match(final KEY_TYPE key) throws Exception {
        int i = search(key);
        if (i > -1) {
            return i;
        }
        i = -i - 1;
        return i;
    }

    /**
     * Locate the entry with the first element &gt;= a key.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or -1.
     */
    public final int ceiling(final KEY_TYPE key) throws Exception {
        final int i = match(key);
        if (i == size()) {
            return -1;
        }
        return i;
    }

    @Override
    public AsyncRequest<Boolean> kMakeReq(final KEY_TYPE _key) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(kMake(_key));
            }
        };
    }

    /**
     * Add an entry to the map unless there is an entry with a matching first element.
     *
     * @param key Used to match the first element of the entries.
     * @return True if a new entry was created.
     */
    @Override
    public final Boolean kMake(final KEY_TYPE key) throws Exception {
        int i = search(key);
        if (i > -1) {
            return false;
        }
        i = -i - 1;
        iAdd(i);
        final MapEntryImpl<KEY_TYPE, VALUE_TYPE> me = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
        me.setKey(key);
        return true;
    }

    @Override
    public AsyncRequest<Boolean> kMakeReq(final KEY_TYPE _key,
            final byte[] _bytes) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(kMake(_key, _bytes));
            }
        };
    }

    /**
     * Add a tuple to the map unless there is a tuple with a matching first element.
     *
     * @param key   Used to match the first element of the tuples.
     * @param bytes The serialized form of a JID of the appropriate type.
     * @return True if a new tuple was created; otherwise the old value is unaltered.
     */
    @Override
    public Boolean kMake(final KEY_TYPE key, final byte[] bytes)
            throws Exception {
        if (!kMake(key)) {
            return false;
        }
        kSet(key, bytes);
        return true;
    }

    public final MapEntryImpl<KEY_TYPE, VALUE_TYPE> kGetEntry(final KEY_TYPE key)
            throws Exception {
        final int i = search(key);
        if (i < 0) {
            return null;
        }
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<VALUE_TYPE> kGetReq(final KEY_TYPE _key) {
        return new AsyncBladeRequest<VALUE_TYPE>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(kGet(_key));
            }
        };
    }

    /**
     * Returns the JID value associated with the key.
     *
     * @param key The key.
     * @return The jid assigned to the key, or null.
     */
    @Override
    public final VALUE_TYPE kGet(final KEY_TYPE key) throws Exception {
        final MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(
            final KEY_TYPE _key) {
        return new AsyncBladeRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getHigher(_key));
            }
        };
    }

    /**
     * Returns the JID value with a greater key.
     *
     * @param key The key.
     * @return The matching jid, or null.
     */
    @Override
    public final MapEntryImpl<KEY_TYPE, VALUE_TYPE> getHigher(final KEY_TYPE key)
            throws Exception {
        final int i = higher(key);
        if (i < 0) {
            return null;
        }
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(
            final KEY_TYPE _key) {
        return new AsyncBladeRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getCeiling(_key));
            }
        };
    }

    /**
     * Returns the JID value with the smallest key &gt;= the given key.
     *
     * @param key The key.
     * @return The matching jid, or null.
     */
    @Override
    public final MapEntryImpl<KEY_TYPE, VALUE_TYPE> getCeiling(
            final KEY_TYPE key) throws Exception {
        final int i = ceiling(key);
        if (i < 0) {
            return null;
        }
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<Boolean> kRemoveReq(final KEY_TYPE _key) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(kRemove(_key));
            }
        };
    }

    /**
     * Removes the item identified by the key.
     *
     * @param key The key.
     * @return True when the item was present and removed.
     */
    @Override
    public final boolean kRemove(final KEY_TYPE key) throws Exception {
        final int i = search(key);
        if (i < 0) {
            return false;
        }
        iRemove(i);
        return true;
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public final JASerializable resolvePathname(final String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        int s = pathname.indexOf("/");
        if (s == -1) {
            s = pathname.length();
        }
        if (s == 0) {
            throw new IllegalArgumentException("pathname " + pathname);
        }
        final String ns = pathname.substring(0, s);
        final JASerializable jid = kGet(stringToKey(ns));
        if (jid == null) {
            return null;
        }
        if (s == pathname.length()) {
            return jid;
        }
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    @Override
    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getFirst() throws Exception {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(0);
    }

    @Override
    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getLast() throws Exception {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(-1);
    }

    public KEY_TYPE getLastKey() throws Exception {
        return getLast().getKey();
    }

    @Override
    public AsyncRequest<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                kSet(_key, _bytes);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void kSet(final KEY_TYPE key, final byte[] bytes) throws Exception {
        final MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null) {
            throw new IllegalArgumentException("not present: " + key);
        }
        entry.setValueBytes(bytes);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
