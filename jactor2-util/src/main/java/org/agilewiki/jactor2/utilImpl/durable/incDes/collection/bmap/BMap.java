package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.Collection;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAMap;
import org.agilewiki.jactor2.util.durable.incDes.MapEntry;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.app.DurableImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.MapEntryImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap.SMap;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens.JAIntegerImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * A balanced tree that holds a map.
 */
abstract public class BMap<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE extends JASerializable>
        extends DurableImpl implements JAMap<KEY_TYPE, VALUE_TYPE>,
        Collection<MapEntry<KEY_TYPE, VALUE_TYPE>> {
    protected final int TUPLE_SIZE = 0;
    protected final int TUPLE_UNION = 1;
    protected int nodeCapacity = 28;
    protected boolean isRoot;
    public Factory valueFactory;
    protected FactoryLocator factoryLocator;

    @Override
    public AsyncRequest<Integer> sizeReq() {
        return new AsyncBladeRequest<Integer>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(size());
            }
        };
    }

    @Override
    public AsyncRequest<Void> emptyReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                empty();
                processAsyncResponse(null);
            }
        };
    }

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
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    abstract protected KEY_TYPE stringToKey(String skey);

    protected Factory getValueFactory() {
        if (valueFactory == null) {
            throw new IllegalStateException("valueFactory uninitialized");
        }
        return valueFactory;
    }

    protected void init() throws Exception {
        String baseType = getFactoryName();
        if (baseType.startsWith("IN.")) {
            baseType = baseType.substring(3);
        }
        factoryLocator = Durables.getFactoryLocator(getReactor());
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_SIZE] = factoryLocator
                .getFactory(JAInteger.FACTORY_NAME);
        tupleFactories[TUPLE_UNION] = factoryLocator
                .getFactory("U." + baseType);
    }

    protected void setNodeLeaf() throws Exception {
        getUnionJid().setValue(0);
    }

    protected void setNodeFactory(final FactoryImpl factoryImpl)
            throws Exception {
        getUnionJid().setValue(factoryImpl);
    }

    protected JAIntegerImpl getSizeJid() throws Exception {
        return (JAIntegerImpl) _iGet(TUPLE_SIZE);
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size() throws Exception {
        return getSizeJid().getValue();
    }

    protected void incSize(final int inc) throws Exception {
        final JAIntegerImpl sj = getSizeJid();
        sj.setValue(sj.getValue() + inc);
    }

    protected UnionImpl getUnionJid() throws Exception {
        return (UnionImpl) _iGet(TUPLE_UNION);
    }

    protected SMap<KEY_TYPE, JASerializable> getNode() throws Exception {
        return (SMap) getUnionJid().getValue();
    }

    public String getNodeFactoryKey() throws Exception {
        return getNode().getFactory().getFactoryKey();
    }

    public boolean isLeaf() throws Exception {
        return getNodeFactoryKey().startsWith("LM.");
    }

    public int nodeSize() throws Exception {
        return getNode().size();
    }

    public boolean isFat() throws Exception {
        return nodeSize() >= nodeCapacity;
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> iGetReq(final int _i) {
        return new AsyncBladeRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(iGet(_i));
            }
        };
    }

    /**
     * Returns the selected element.
     *
     * @param ndx Selects the element.
     * @return The ith JID component, or null if the index is out of range.
     */
    @Override
    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> iGet(int ndx) throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(ndx);
        }
        if (ndx < 0) {
            ndx += size();
        }
        if ((ndx < 0) || (ndx >= size())) {
            return null;
        }
        int i = 0;
        while (i < node.size()) {
            final BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node
                    .iGet(i)).getValue();
            final int bns = bnode.size();
            if (ndx < bns) {
                return bnode.iGet(ndx);
            }
            ndx -= bns;
            i += 1;
        }
        return null;
    }

    @Override
    public AsyncRequest<Void> iSetReq(final int _i, final byte[] _bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iSet(_i, _bytes);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param ndx   The index of the desired element.
     * @param bytes Holds the serialized data.
     */
    @Override
    public void iSet(final int ndx, final byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iAdd(_i);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(final int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iAdd(_i, _bytes);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(final int ndx, final byte[] bytes) {
        throw new UnsupportedOperationException();
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
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            int i = node.search(key);
            if (i > -1) {
                return false;
            }
            i = -i - 1;
            node.iAdd(i);
            final MapEntryImpl<KEY_TYPE, VALUE_TYPE> me = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node
                    .iGet(i);
            me.setKey(key);
            incSize(1);
            return true;
        }
        int i = node.match(key);
        MapEntryImpl<KEY_TYPE, VALUE_TYPE> entry = null;
        if (node.size() == i) {
            i -= 1;
            entry = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(i);
            entry.setKey(key);
        } else {
            entry = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(i);
        }
        final BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
        if (!bnode.kMake(key)) {
            return false;
        }
        incSize(1);
        if (bnode.isFat()) {
            node.iAdd(i - 1);
            final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry = (MapEntryImpl) node
                    .iGet(i - 1);
            bnode.inodeSplit(leftEntry);
            if (node.size() < nodeCapacity) {
                return true;
            }
            if (isRoot) {
                rootSplit();
            }
        }
        return true;
    }

    protected void rootSplit() throws Exception {
        final SMap<KEY_TYPE, JASerializable> oldRootNode = getNode();
        final FactoryImpl oldFactory = oldRootNode.getFactory();
        getUnionJid().setValue(1);
        final SMap<KEY_TYPE, JASerializable> newRootNode = getNode();
        newRootNode.iAdd(0);
        newRootNode.iAdd(1);
        final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry = (MapEntryImpl) newRootNode
                .iGet(0);
        final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> rightEntry = (MapEntryImpl) newRootNode
                .iGet(1);
        final BMap<KEY_TYPE, JASerializable> leftBNode = leftEntry.getValue();
        final BMap<KEY_TYPE, JASerializable> rightBNode = rightEntry.getValue();
        leftBNode.setNodeFactory(oldFactory);
        rightBNode.setNodeFactory(oldFactory);
        final int h = nodeCapacity / 2;
        int i = 0;
        if (oldFactory.name.startsWith("LM.")) {
            while (i < h) {
                final JASerializable e = oldRootNode.iGet(i);
                final byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            while (i < nodeCapacity) {
                final JASerializable e = oldRootNode.iGet(i);
                final byte[] bytes = e.getDurable().getSerializedBytes();
                rightBNode.iAdd(-1, bytes);
                i += 1;
            }
        } else {
            while (i < h) {
                final BMap<KEY_TYPE, JASerializable> e = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) oldRootNode
                        .iGet(i)).getValue();
                final int eSize = e.size();
                final byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
            while (i < nodeCapacity) {
                final BMap<KEY_TYPE, JASerializable> e = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) oldRootNode
                        .iGet(i)).getValue();
                final int eSize = e.size();
                final byte[] bytes = e.getSerializedBytes();
                rightBNode.append(bytes, eSize);
                i += 1;
            }
        }
        leftEntry.setKey(leftBNode.getLastKey());
        rightEntry.setKey(rightBNode.getLastKey());
    }

    protected void inodeSplit(
            final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry)
            throws Exception {
        final BMap<KEY_TYPE, JASerializable> leftBNode = leftEntry.getValue();
        leftBNode.setNodeFactory(getNode().getFactory());
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        final int h = nodeCapacity / 2;
        int i = 0;
        if (isLeaf()) {
            while (i < h) {
                final JASerializable e = node.iGet(0);
                node.iRemove(0);
                final byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            incSize(-h);
        } else {
            while (i < h) {
                final BMap<KEY_TYPE, VALUE_TYPE> e = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node
                        .iGet(0)).getValue();
                node.iRemove(0);
                final int eSize = e.size();
                incSize(-eSize);
                final byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
        }
        final KEY_TYPE leftKey = leftBNode.getLastKey();
        leftEntry.setKey(leftKey);
    }

    @Override
    public void empty() throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        node.empty();
        final JAIntegerImpl sj = getSizeJid();
        sj.setValue(0);
    }

    @Override
    public AsyncRequest<Void> iRemoveReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iRemove(_i);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iRemove(int ndx) throws Exception {
        final int s = size();
        if (ndx < 0) {
            ndx += s;
        }
        if ((ndx < 0) || (ndx >= s)) {
            throw new IllegalArgumentException();
        }
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            node.iRemove(ndx);
            incSize(-1);
            return;
        }
        int i = 0;
        while (i < node.size()) {
            final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> entry = (MapEntryImpl) node
                    .iGet(ndx);
            BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
            final int bns = bnode.size();
            if (ndx < bns) {
                bnode.iRemove(ndx);
                incSize(-1);
                final int bnodeSize = bnode.size();
                if (bnodeSize > (nodeCapacity / 3)) {
                    entry.setKey(bnode.getLastKey());
                    return;
                }
                if (bnodeSize == 0) {
                    node.iRemove(ndx);
                } else {
                    entry.setKey(bnode.getLastKey());
                    if (i > 0) {
                        final MapEntryImpl leftEntry = (MapEntryImpl) node
                                .iGet(i - 1);
                        final BMap<KEY_TYPE, VALUE_TYPE> leftBNode = (BMap) leftEntry
                                .getValue();
                        if ((leftBNode.nodeSize() + bnodeSize) < nodeCapacity) {
                            bnode.appendTo(leftBNode);
                            node.iRemove(i);
                            leftEntry.setKey(leftBNode.getLastKey());
                        }
                    }
                    if ((i + 1) < node.size()) {
                        final MapEntryImpl rightEntry = (MapEntryImpl) node
                                .iGet(i + 1);
                        final BMap<KEY_TYPE, VALUE_TYPE> rightBNode = (BMap) rightEntry
                                .getValue();
                        if ((bnodeSize + rightBNode.nodeSize()) < nodeCapacity) {
                            rightBNode.appendTo(bnode);
                            node.iRemove(i + 1);
                            rightEntry.setKey(rightBNode.getLastKey());
                        }
                    }
                }
                if ((node.size() == 1) && isRoot && !isLeaf()) {
                    bnode = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node
                            .iGet(0)).getValue();
                    setNodeFactory(bnode.getNode().getFactory());
                    final JAIntegerImpl sj = getSizeJid();
                    sj.setValue(0);
                    bnode.appendTo(this);
                }
                return;
            }
            ndx -= bns;
            i += 1;
        }
        throw new IllegalArgumentException();
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
        if (isLeaf()) {
            final SMap<KEY_TYPE, JASerializable> node = getNode();
            if (node.kRemove(key)) {
                incSize(-1);
                return true;
            }
            return false;
        }
        final SMap<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> node = (SMap) getNode();
        final int i = node.match(key);
        if (i == size()) {
            return false;
        }
        final MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> entry = (MapEntryImpl) node
                .iGet(i);
        BMap<KEY_TYPE, JASerializable> bnode = entry.getValue();
        if (!bnode.kRemove(key)) {
            return false;
        }
        incSize(-1);
        final int bnodeSize = bnode.size();
        if (bnodeSize > (nodeCapacity / 3)) {
            return true;
        }
        if (bnodeSize == 0) {
            node.iRemove(i);
        } else {
            entry.setKey(bnode.getLastKey());
            if (i > 0) {
                final MapEntryImpl leftEntry = (MapEntryImpl) node.iGet(i - 1);
                final BMap<KEY_TYPE, VALUE_TYPE> leftBNode = (BMap) leftEntry
                        .getValue();
                if ((leftBNode.nodeSize() + bnodeSize) < nodeCapacity) {
                    bnode.appendTo((BMap<KEY_TYPE, JASerializable>) leftBNode);
                    node.iRemove(i);
                    leftEntry.setKey(leftBNode.getLastKey());
                }
            }
            if ((i + 1) < node.size()) {
                final MapEntryImpl rightEntry = (MapEntryImpl) node.iGet(i + 1);
                final BMap<KEY_TYPE, VALUE_TYPE> rightBNode = (BMap) rightEntry
                        .getValue();
                if ((bnodeSize + rightBNode.nodeSize()) < nodeCapacity) {
                    rightBNode.appendTo((BMap<KEY_TYPE, VALUE_TYPE>) bnode);
                    node.iRemove(i + 1);
                    rightEntry.setKey(rightBNode.getLastKey());
                }
            }
        }
        if ((node.size() == 1) && isRoot && !isLeaf()) {
            bnode = (BMap) ((MapEntryImpl) node.iGet(0)).getValue();
            setNodeFactory(bnode.getNode().getFactory());
            final JAIntegerImpl sj = getSizeJid();
            sj.setValue(0);
            bnode.appendTo((BMap<KEY_TYPE, JASerializable>) this);
        }
        return true;
    }

    void appendTo(final BMap<KEY_TYPE, VALUE_TYPE> leftNode) throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        int i = 0;
        if (isLeaf()) {
            while (i < node.size()) {
                final JASerializable e = node.iGet(i);
                leftNode.append(e.getDurable().getSerializedBytes(), 1);
                i += 1;
            }
        } else {
            while (i < node.size()) {
                final BMap<KEY_TYPE, VALUE_TYPE> e = (BMap) ((MapEntryImpl) node
                        .iGet(i)).getValue();
                leftNode.append(e.getSerializedBytes(), e.size());
                i += 1;
            }
        }
    }

    void append(final byte[] bytes, final int eSize) throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        node.iAdd(-1, bytes);
        incSize(eSize);
    }

    public final MapEntryImpl<KEY_TYPE, VALUE_TYPE> kGetEntry(final KEY_TYPE key)
            throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            final int i = node.search(key);
            if (i < 0) {
                return null;
            }
            return iGet(i);
        }
        final int i = node.match(key);
        if (i == size()) {
            return null;
        }
        final BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl) node
                .iGet(i)).getValue();
        return bnode.kGetEntry(key);
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
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.getCeiling(key);
        }
        final int i = node.match(key);
        if (i == size()) {
            return null;
        }
        final BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl) node
                .iGet(i)).getValue();
        return bnode.getCeiling(key);
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
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        final MapEntryImpl entry = node.getHigher(key);
        if (isLeaf()) {
            return entry;
        }
        if (entry == null) {
            return null;
        }
        final BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
        return bnode.getHigher(key);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     * @throws Exception Any uncaught exception which occurred while processing the request.
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
        return iGet(0);
    }

    @Override
    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getLast() throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.getLast();
    }

    public KEY_TYPE getLastKey() throws Exception {
        final SMap<KEY_TYPE, JASerializable> node = getNode();
        return node.getLastKey();
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
