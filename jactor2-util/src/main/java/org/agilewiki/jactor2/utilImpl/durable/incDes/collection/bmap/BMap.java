package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
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
        extends DurableImpl
        implements JAMap<KEY_TYPE, VALUE_TYPE>, Collection<MapEntry<KEY_TYPE, VALUE_TYPE>> {
    protected final int TUPLE_SIZE = 0;
    protected final int TUPLE_UNION = 1;
    protected int nodeCapacity = 28;
    protected boolean isRoot;
    public Factory valueFactory;
    protected FactoryLocator factoryLocator;

    @Override
    public AsyncRequest<Integer> sizeReq() {
        return new AsyncRequest<Integer>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(size());
            }
        };
    }

    public AsyncRequest<Void> emptyReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                empty();
                processResponse(null);
            }
        };
    }

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
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    abstract protected KEY_TYPE stringToKey(String skey);

    protected Factory getValueFactory() {
        if (valueFactory == null)
            throw new IllegalStateException("valueFactory uninitialized");
        return valueFactory;
    }

    protected void init() throws Exception {
        String baseType = getFactoryName();
        if (baseType.startsWith("IN."))
            baseType = baseType.substring(3);
        factoryLocator = Durables.getFactoryLocator(getMessageProcessor());
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_SIZE] = factoryLocator.getFactory(JAInteger.FACTORY_NAME);
        tupleFactories[TUPLE_UNION] = factoryLocator.getFactory("U." + baseType);
    }

    protected void setNodeLeaf()
            throws Exception {
        getUnionJid().setValue(0);
    }

    protected void setNodeFactory(FactoryImpl factoryImpl)
            throws Exception {
        getUnionJid().setValue(factoryImpl);
    }

    protected JAIntegerImpl getSizeJid()
            throws Exception {
        return (JAIntegerImpl) _iGet(TUPLE_SIZE);
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size()
            throws Exception {
        return getSizeJid().getValue();
    }

    protected void incSize(int inc)
            throws Exception {
        JAIntegerImpl sj = getSizeJid();
        sj.setValue(sj.getValue() + inc);
    }

    protected UnionImpl getUnionJid()
            throws Exception {
        return (UnionImpl) _iGet(TUPLE_UNION);
    }

    protected SMap<KEY_TYPE, JASerializable> getNode()
            throws Exception {
        return (SMap) getUnionJid().getValue();
    }

    public String getNodeFactoryKey()
            throws Exception {
        return getNode().getFactory().getFactoryKey();
    }

    public boolean isLeaf()
            throws Exception {
        return getNodeFactoryKey().startsWith("LM.");
    }

    public int nodeSize()
            throws Exception {
        return getNode().size();
    }

    public boolean isFat()
            throws Exception {
        return nodeSize() >= nodeCapacity;
    }

    @Override
    public AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>> iGetReq(final int _i) {
        return new AsyncRequest<MapEntry<KEY_TYPE, VALUE_TYPE>>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                processResponse(iGet(_i));
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
    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> iGet(int ndx)
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(ndx);
        }
        if (ndx < 0)
            ndx += size();
        if (ndx < 0 || ndx >= size())
            return null;
        int i = 0;
        while (i < node.size()) {
            BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(i)).getValue();
            int bns = bnode.size();
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
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iSet(_i, _bytes);
                processResponse(null);
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
    public void iSet(int ndx, byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iAdd(_i);
                processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iAdd(_i, _bytes);
                processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int ndx, byte[] bytes) {
        throw new UnsupportedOperationException();
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
        SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            int i = node.search(key);
            if (i > -1)
                return false;
            i = -i - 1;
            node.iAdd(i);
            MapEntryImpl<KEY_TYPE, VALUE_TYPE> me = (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(i);
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
        BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
        if (!bnode.kMake(key))
            return false;
        incSize(1);
        if (bnode.isFat()) {
            node.iAdd(i - 1);
            MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry = (MapEntryImpl) node.iGet(i - 1);
            bnode.inodeSplit(leftEntry);
            if (node.size() < nodeCapacity)
                return true;
            if (isRoot) {
                rootSplit();
            }
        }
        return true;
    }

    protected void rootSplit()
            throws Exception {
        SMap<KEY_TYPE, JASerializable> oldRootNode = getNode();
        FactoryImpl oldFactory = oldRootNode.getFactory();
        getUnionJid().setValue(1);
        SMap<KEY_TYPE, JASerializable> newRootNode = getNode();
        newRootNode.iAdd(0);
        newRootNode.iAdd(1);
        MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry = (MapEntryImpl) newRootNode.iGet(0);
        MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> rightEntry = (MapEntryImpl) newRootNode.iGet(1);
        BMap<KEY_TYPE, JASerializable> leftBNode = leftEntry.getValue();
        BMap<KEY_TYPE, JASerializable> rightBNode = rightEntry.getValue();
        leftBNode.setNodeFactory(oldFactory);
        rightBNode.setNodeFactory(oldFactory);
        int h = nodeCapacity / 2;
        int i = 0;
        if (oldFactory.name.startsWith("LM.")) {
            while (i < h) {
                JASerializable e = oldRootNode.iGet(i);
                byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            while (i < nodeCapacity) {
                JASerializable e = oldRootNode.iGet(i);
                byte[] bytes = e.getDurable().getSerializedBytes();
                rightBNode.iAdd(-1, bytes);
                i += 1;
            }
        } else {
            while (i < h) {
                BMap<KEY_TYPE, JASerializable> e =
                        (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) oldRootNode.iGet(i)).getValue();
                int eSize = e.size();
                byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
            while (i < nodeCapacity) {
                BMap<KEY_TYPE, JASerializable> e =
                        (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) oldRootNode.iGet(i)).getValue();
                int eSize = e.size();
                byte[] bytes = e.getSerializedBytes();
                rightBNode.append(bytes, eSize);
                i += 1;
            }
        }
        leftEntry.setKey(leftBNode.getLastKey());
        rightEntry.setKey(rightBNode.getLastKey());
    }

    protected void inodeSplit(MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> leftEntry)
            throws Exception {
        BMap<KEY_TYPE, JASerializable> leftBNode = leftEntry.getValue();
        leftBNode.setNodeFactory(getNode().getFactory());
        SMap<KEY_TYPE, JASerializable> node = getNode();
        int h = nodeCapacity / 2;
        int i = 0;
        if (isLeaf()) {
            while (i < h) {
                JASerializable e = node.iGet(0);
                node.iRemove(0);
                byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            incSize(-h);
        } else {
            while (i < h) {
                BMap<KEY_TYPE, VALUE_TYPE> e = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(0)).getValue();
                node.iRemove(0);
                int eSize = e.size();
                incSize(-eSize);
                byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
        }
        KEY_TYPE leftKey = leftBNode.getLastKey();
        leftEntry.setKey(leftKey);
    }

    @Override
    public void empty()
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        node.empty();
        JAIntegerImpl sj = getSizeJid();
        sj.setValue(0);
    }

    @Override
    public AsyncRequest<Void> iRemoveReq(final int _i) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iRemove(_i);
                processResponse(null);
            }
        };
    }

    @Override
    public void iRemove(int ndx)
            throws Exception {
        int s = size();
        if (ndx < 0)
            ndx += s;
        if (ndx < 0 || ndx >= s)
            throw new IllegalArgumentException();
        SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            node.iRemove(ndx);
            incSize(-1);
            return;
        }
        int i = 0;
        while (i < node.size()) {
            MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> entry = (MapEntryImpl) node.iGet(ndx);
            BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
            int bns = bnode.size();
            if (ndx < bns) {
                bnode.iRemove(ndx);
                incSize(-1);
                int bnodeSize = bnode.size();
                if (bnodeSize > nodeCapacity / 3) {
                    entry.setKey(bnode.getLastKey());
                    return;
                }
                if (bnodeSize == 0) {
                    node.iRemove(ndx);
                } else {
                    entry.setKey(bnode.getLastKey());
                    if (i > 0) {
                        MapEntryImpl leftEntry = (MapEntryImpl) node.iGet(i - 1);
                        BMap<KEY_TYPE, VALUE_TYPE> leftBNode = (BMap) leftEntry.getValue();
                        if (leftBNode.nodeSize() + bnodeSize < nodeCapacity) {
                            bnode.appendTo(leftBNode);
                            node.iRemove(i);
                            leftEntry.setKey(leftBNode.getLastKey());
                        }
                    }
                    if (i + 1 < node.size()) {
                        MapEntryImpl rightEntry = (MapEntryImpl) node.iGet(i + 1);
                        BMap<KEY_TYPE, VALUE_TYPE> rightBNode = (BMap) rightEntry.getValue();
                        if (bnodeSize + rightBNode.nodeSize() < nodeCapacity) {
                            rightBNode.appendTo(bnode);
                            node.iRemove(i + 1);
                            rightEntry.setKey(rightBNode.getLastKey());
                        }
                    }
                }
                if (node.size() == 1 && isRoot && !isLeaf()) {
                    bnode = (BMap) ((MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.iGet(0)).getValue();
                    setNodeFactory(bnode.getNode().getFactory());
                    JAIntegerImpl sj = getSizeJid();
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
        if (isLeaf()) {
            SMap<KEY_TYPE, JASerializable> node = getNode();
            if (node.kRemove(key)) {
                incSize(-1);
                return true;
            }
            return false;
        }
        SMap<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> node = (SMap) getNode();
        int i = node.match(key);
        if (i == size())
            return false;
        MapEntryImpl<KEY_TYPE, BMap<KEY_TYPE, JASerializable>> entry = (MapEntryImpl) node.iGet(i);
        BMap<KEY_TYPE, JASerializable> bnode = entry.getValue();
        if (!bnode.kRemove(key))
            return false;
        incSize(-1);
        int bnodeSize = bnode.size();
        if (bnodeSize > nodeCapacity / 3)
            return true;
        if (bnodeSize == 0) {
            node.iRemove(i);
        } else {
            entry.setKey(bnode.getLastKey());
            if (i > 0) {
                MapEntryImpl leftEntry = (MapEntryImpl) node.iGet(i - 1);
                BMap<KEY_TYPE, VALUE_TYPE> leftBNode = (BMap) leftEntry.getValue();
                if (leftBNode.nodeSize() + bnodeSize < nodeCapacity) {
                    bnode.appendTo((BMap<KEY_TYPE, JASerializable>) leftBNode);
                    node.iRemove(i);
                    leftEntry.setKey(leftBNode.getLastKey());
                }
            }
            if (i + 1 < node.size()) {
                MapEntryImpl rightEntry = (MapEntryImpl) node.iGet(i + 1);
                BMap<KEY_TYPE, VALUE_TYPE> rightBNode = (BMap) rightEntry.getValue();
                if (bnodeSize + rightBNode.nodeSize() < nodeCapacity) {
                    rightBNode.appendTo((BMap<KEY_TYPE, VALUE_TYPE>) bnode);
                    node.iRemove(i + 1);
                    rightEntry.setKey(rightBNode.getLastKey());
                }
            }
        }
        if (node.size() == 1 && isRoot && !isLeaf()) {
            bnode = (BMap) ((MapEntryImpl) node.iGet(0)).getValue();
            setNodeFactory(bnode.getNode().getFactory());
            JAIntegerImpl sj = getSizeJid();
            sj.setValue(0);
            bnode.appendTo((BMap<KEY_TYPE, JASerializable>) this);
        }
        return true;
    }

    void appendTo(BMap<KEY_TYPE, VALUE_TYPE> leftNode)
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        int i = 0;
        if (isLeaf()) {
            while (i < node.size()) {
                JASerializable e = node.iGet(i);
                leftNode.append(e.getDurable().getSerializedBytes(), 1);
                i += 1;
            }
        } else {
            while (i < node.size()) {
                BMap<KEY_TYPE, VALUE_TYPE> e = (BMap) ((MapEntryImpl) node.iGet(i)).getValue();
                leftNode.append(e.getSerializedBytes(), e.size());
                i += 1;
            }
        }
    }

    void append(byte[] bytes, int eSize)
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        node.iAdd(-1, bytes);
        incSize(eSize);
    }

    final public MapEntryImpl<KEY_TYPE, VALUE_TYPE> kGetEntry(KEY_TYPE key)
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            int i = node.search(key);
            if (i < 0)
                return null;
            return iGet(i);
        }
        int i = node.match(key);
        if (i == size())
            return null;
        BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl) node.iGet(i)).getValue();
        return bnode.kGetEntry(key);
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
        SMap<KEY_TYPE, JASerializable> node = getNode();
        if (isLeaf()) {
            return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.getCeiling(key);
        }
        int i = node.match(key);
        if (i == size())
            return null;
        BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) ((MapEntryImpl) node.iGet(i)).getValue();
        return bnode.getCeiling(key);
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
        SMap<KEY_TYPE, JASerializable> node = getNode();
        MapEntryImpl entry = node.getHigher(key);
        if (isLeaf())
            return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) entry;
        if (entry == null)
            return null;
        BMap<KEY_TYPE, VALUE_TYPE> bnode = (BMap) entry.getValue();
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
        return iGet(0);
    }

    public MapEntryImpl<KEY_TYPE, VALUE_TYPE> getLast()
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        return (MapEntryImpl<KEY_TYPE, VALUE_TYPE>) node.getLast();
    }

    public KEY_TYPE getLastKey()
            throws Exception {
        SMap<KEY_TYPE, JASerializable> node = getNode();
        return node.getLastKey();
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
