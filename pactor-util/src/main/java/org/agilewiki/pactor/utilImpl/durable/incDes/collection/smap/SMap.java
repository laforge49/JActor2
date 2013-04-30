package org.agilewiki.pactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.util.durable.incDes.Collection;
import org.agilewiki.pactor.util.durable.incDes.MapEntry;
import org.agilewiki.pactor.util.durable.incDes.PAMap;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.MapEntryImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.slist.SList;

/**
 * Holds a map.
 */
abstract public class SMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends PASerializable>
        extends SList<MapEntry<KEY_TYPE, VALUE_TYPE>>
        implements PAMap<KEY_TYPE, VALUE_TYPE>, Collection<MapEntry<KEY_TYPE, VALUE_TYPE>> {

    public Factory valueFactory;

    private Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq;
    private Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq;

    public Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getFirstReq() {
        return getFirstReq;
    }

    public Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getLastReq() {
        return getLastReq;
    }

    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    abstract protected Factory getKeyFactory();

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
    final protected Factory getEntryFactory() {
        Factory af = Durables.getFactory(Durables.getFactoryLocator(getMailbox()), "E." + getType());
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
    final public int search(KEY_TYPE key) {
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
    final public int higher(KEY_TYPE key) {
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
    final public int match(KEY_TYPE key) {
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
    final public int ceiling(KEY_TYPE key) {
        int i = match(key);
        if (i == size())
            return -1;
        return i;
    }

    @Override
    public Request<Boolean> kMakeReq(final KEY_TYPE _key) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport<Boolean> _rp) throws Exception {
                _rp.processResponse(kMake(_key));
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
    final public Boolean kMake(KEY_TYPE key) {
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
    public Request<Boolean> kMakeReq(final KEY_TYPE _key, final byte[] _bytes) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport<Boolean> _rp) throws Exception {
                _rp.processResponse(kMake(_key, _bytes));
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
    public Boolean kMake(KEY_TYPE key, byte[] bytes) {
        if (!kMake(key))
            return false;
        kSet(key, bytes);
        return true;
    }

    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> kGetEntry(KEY_TYPE key) {
        int i = search(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public Request<VALUE_TYPE> kGetReq(final KEY_TYPE _key) {
        return new RequestBase<VALUE_TYPE>(getMailbox()) {
            @Override
            public void processRequest(Transport<VALUE_TYPE> _rp) throws Exception {
                _rp.processResponse(kGet(_key));
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
    final public VALUE_TYPE kGet(KEY_TYPE key) {
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null)
            return null;
        return entry.getValue();
    }

    @Override
    public Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getHigherReq(final KEY_TYPE _key) {
        return new RequestBase<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMailbox()) {
            @Override
            public void processRequest(Transport<MapEntry<KEY_TYPE, VALUE_TYPE>> _rp) throws Exception {
                _rp.processResponse(getHigher(_key));
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
    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getHigher(KEY_TYPE key) {
        int i = higher(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public Request<MapEntry<KEY_TYPE, VALUE_TYPE>> getCeilingReq(final KEY_TYPE _key) {
        return new RequestBase<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMailbox()) {
            @Override
            public void processRequest(Transport<MapEntry<KEY_TYPE, VALUE_TYPE>> _rp) throws Exception {
                _rp.processResponse(getCeiling(_key));
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
    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getCeiling(KEY_TYPE key) {
        int i = ceiling(key);
        if (i < 0)
            return null;
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(i);
    }

    @Override
    public Request<Boolean> kRemoveReq(final KEY_TYPE _key) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport<Boolean> _rp) throws Exception {
                _rp.processResponse(kRemove(_key));
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
    final public boolean kRemove(KEY_TYPE key) {
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
    final public PASerializable resolvePathname(String pathname) {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        int s = pathname.indexOf("/");
        if (s == -1)
            s = pathname.length();
        if (s == 0)
            throw new IllegalArgumentException("pathname " + pathname);
        String ns = pathname.substring(0, s);
        PASerializable jid = kGet(stringToKey(ns));
        if (jid == null)
            return null;
        if (s == pathname.length())
            return jid;
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getFirst() {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(0);
    }

    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getLast() {
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) iGet(-1);
    }

    public KEY_TYPE getLastKey() {
        return getLast().getKey();
    }

    @Override
    public Request<Void> kSetReq(final KEY_TYPE _key, final byte[] _bytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _rp) throws Exception {
                kSet(_key, _bytes);
                _rp.processResponse(null);
            }
        };
    }

    @Override
    public void kSet(KEY_TYPE key, byte[] bytes) {
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = kGetEntry(key);
        if (entry == null)
            throw new IllegalArgumentException("not present: " + key);
        entry.setValueBytes(bytes);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        getFirstReq = new RequestBase<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMailbox()) {
            @Override
            public void processRequest(Transport<MapEntry<KEY_TYPE, VALUE_TYPE>> _rp) throws Exception {
                _rp.processResponse(getFirst());
            }
        };
        getLastReq = new RequestBase<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMailbox()) {
            @Override
            public void processRequest(Transport<MapEntry<KEY_TYPE, VALUE_TYPE>> _rp) throws Exception {
                _rp.processResponse(getLast());
            }
        };
    }
}
