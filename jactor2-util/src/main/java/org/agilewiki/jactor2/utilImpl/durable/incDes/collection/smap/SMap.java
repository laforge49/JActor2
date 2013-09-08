package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
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
        extends SList<MapEntry<KEY_TYPE, VALUE_TYPE>>
        implements JAMap<KEY_TYPE, VALUE_TYPE>, Collection<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    public Factory valueFactory;

    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq() {
        return new AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(getFirst());
            }
        };
    }

    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq() {
        return new AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(getLast());
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
        Factory af = Durables.getFactoryLocator(getMessageProcessor()).getFactory("E." + getFactoryName());
        return af;
    }

    /**
     * Returns the IncDesFactory for the values in the map.
     *
     * @return The jid factory of the values in the list.
     */
    protected Factory getValueFactory() {
        if (valueFactory != null)
            return valueFactory;
        throw new IllegalStateException("valueFactory not set");
    }

    /**
     * Locate the entry with a matching first element.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or - (insertion point + 1).
     */
    final public int search(KEY_TYPE key)
            throws Exception {
        initializeList();
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            MapEntryImpl<KEY_TYPE, VALUE_TYPE> midVal = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(mid);
            int c = midVal.compareKeyTo(key);
            if (c < 0)
                low = mid + 1;
            else if (c > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    /**
     * Locate the entry with a higher key.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or -1.
     */
    final public int higher(KEY_TYPE key)
            throws Exception {
        int i = search(key);
        if (i > -1)
            i += 1;
        else {
            i = -i - 1;
        }
        if (i == size())
            return -1;
        return i;
    }

    /**
     * Locate the entry with the first element >= a key, or the last entry.
     *
     * @param key The key which matches to the entry's first element, or size.
     * @return The index, or size.
     */
    final public int match(KEY_TYPE key)
            throws Exception {
        int i = search(key);
        if (i > -1)
            return i;
        i = -i - 1;
        return i;
    }

    /**
     * Locate the entry with the first element >= a key.
     *
     * @param key The key which matches to the entry's first element.
     * @return The index or -1.
     */
    final public int ceiling(KEY_TYPE key)
            throws Exception {
        int i = match(key);
        if (i == size())
            return -1;
        return i;
    }

    @Override
    public AsyncRequest<Boolean> kMakeReq(final KEY_TYPE _key) {
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(kMake(_key));
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
    final public Boolean kMake(KEY_TYPE key)
            throws Exception {
        int i = search(key);
        if (i > -1)
            return false;
        i = -i - 1;
        iAdd(i);
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> me = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
        me.setKey(key);
        return true;
    }

    @Override
    public AsyncRequest<Boolean> kMakeReq(final KEY_TYPE _key, final byte[] _bytes) {
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(kMake(_key, _bytes));
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
    public Boolean kMake(KEY_TYPE key, byte[] bytes)
            throws Exception {
        if (!kMake(key))
            return false;
        kSet(key, bytes);
        return true;
    }

    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> kGetEntry(KEY_TYPE key)
            throws Exception {
        int i = search(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<VALUE_TYPE> kGetReq(final KEY_TYPE _key) {
        return new AsyncRequest<VALUE_TYPE>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(kGet(_key));
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
    final public VALUE_TYPE kGet(KEY_TYPE key)
            throws Exception {
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null)
            return null;
        return entry.getValue();
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(final KEY_TYPE _key) {
        return new AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(getHigher(_key));
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
    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getHigher(KEY_TYPE key)
            throws Exception {
        int i = higher(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(final KEY_TYPE _key) {
        return new AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(getCeiling(_key));
            }
        };
    }

    /**
     * Returns the JID value with the smallest key >= the given key.
     *
     * @param key The key.
     * @return The matching jid, or null.
     */
    @Override
    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getCeiling(KEY_TYPE key)
            throws Exception {
        int i = ceiling(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public AsyncRequest<Boolean> kRemoveReq(final KEY_TYPE _key) {
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(kRemove(_key));
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
    final public boolean kRemove(KEY_TYPE key)
            throws Exception {
        int i = search(key);
        if (i < 0)
            return false;
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
    final public JASerializable resolvePathname(String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        int s = pathname.indexOf("/");
        if (s == -1)
            s = pathname.length();
        if (s == 0)
            throw new IllegalArgumentException("pathname " + pathname);
        String ns = pathname.substring(0, s);
        JASerializable jid = kGet(stringToKey(ns));
        if (jid == null)
            return null;
        if (s == pathname.length())
            return jid;
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getFirst()
            throws Exception {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(0);
    }

    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getLast()
            throws Exception {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(-1);
    }

    public KEY_TYPE getLastKey()
            throws Exception {
        return getLast().getKey();
    }

    @Override
    public AsyncRequest<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                kSet(_key, _bytes);
                processResponse(null);
            }
        };
    }

    @Override
    public void kSet(KEY_TYPE key, byte[] bytes)
            throws Exception {
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null)
            throw new IllegalArgumentException("not present: " + key);
        entry.setValueBytes(bytes);
    }

    public void initialize(final MessageProcessor messageProcessor, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(messageProcessor, parent, factory);
    }
}
