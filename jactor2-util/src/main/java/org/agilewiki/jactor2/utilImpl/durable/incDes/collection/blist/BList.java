package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.blist;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.Collection;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAList;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.app.DurableImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist.SList;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens.JAIntegerImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * A balanced tree holding a list of JIDs, all of the same type.
 */
public class BList<ENTRY_TYPE extends JASerializable> extends DurableImpl
        implements JAList<ENTRY_TYPE>, Collection<ENTRY_TYPE> {
    protected final int TUPLE_SIZE = 0;
    protected final int TUPLE_UNION = 1;
    protected int nodeCapacity = 28;
    protected boolean isRoot;
    protected Factory entryFactory;
    protected FactoryLocator factoryLocator;

    @Override
    public AsyncRequest<Integer> sizeReq() {
        return new AsyncBladeRequest<Integer>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(size());
            }
        };
    }

    @Override
    public AsyncRequest<Void> emptyReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                empty();
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Returns the IncDesFactory for all the elements in the list.
     *
     * @return The IncDesFactory for of all the elements in the list.
     */
    protected Factory getEntryFactory() {
        if (entryFactory == null) {
            throw new IllegalStateException("entryFactory uninitialized");
        }
        return entryFactory;
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

    protected SList<ENTRY_TYPE> getNode() throws Exception {
        return (SList) getUnionJid().getValue();
    }

    public String getNodeFactoryKey() throws Exception {
        return getNode().getFactory().getFactoryKey();
    }

    public boolean isLeaf() throws Exception {
        return getNodeFactoryKey().startsWith("LL.");
    }

    public int nodeSize() throws Exception {
        return getNode().size();
    }

    public boolean isFat() throws Exception {
        return nodeSize() >= nodeCapacity;
    }

    @Override
    public AsyncRequest<ENTRY_TYPE> iGetReq(final int _i) {
        return new AsyncBladeRequest<ENTRY_TYPE>() {
            @Override
            protected void processAsyncRequest() throws Exception {
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
    public ENTRY_TYPE iGet(int ndx) throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            return node.iGet(ndx);
        }
        if (ndx < 0) {
            ndx += size();
        }
        if ((ndx < 0) || (ndx >= size())) {
            return null;
        }
        int i = 0;
        while (i < node.size()) {
            final BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
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
            protected void processAsyncRequest() throws Exception {
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
    public void iSet(int ndx, final byte[] bytes) throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            node.iSet(ndx, bytes);
            return;
        }
        if (ndx < 0) {
            ndx += size();
        }
        if ((ndx < 0) || (ndx >= size())) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        while (i < node.size()) {
            final BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            final int bns = bnode.size();
            if (ndx < bns) {
                bnode.iSet(ndx, bytes);
                return;
            }
            ndx -= bns;
            i += 1;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public JASerializable resolvePathname(final String pathname)
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
        int n = 0;
        try {
            n = Integer.parseInt(ns);
        } catch (final Exception ex) {
            throw new IllegalArgumentException("pathname " + pathname);
        }
        if ((n < 0) || (n >= size())) {
            throw new IllegalArgumentException("pathname " + pathname);
        }
        final JASerializable jid = iGet(n);
        if (s == pathname.length()) {
            return jid;
        }
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                iAdd(_i);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(final int i) throws Exception {
        iAdd(i, null);
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                iAdd(_i, _bytes);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int ndx, final byte[] bytes) throws Exception {
        if (ndx < 0) {
            ndx = size() + 1 + ndx;
        }
        if ((ndx < 0) || (ndx > size())) {
            throw new IllegalArgumentException();
        }
        incSize(1);
        final SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            if (bytes == null) {
                node.iAdd(ndx);
            } else {
                node.iAdd(ndx, bytes);
            }
            if (node.size() < nodeCapacity) {
                return;
            }
            if (isRoot) {
                rootSplit();
                return;
            }
            return;
        }
        int i = 0;
        while (true) {
            final BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            final int bns = bnode.size();
            i += 1;
            if ((ndx < bns) || (i == node.size())) {
                bnode.iAdd(ndx, bytes);
                if (bnode.isFat()) {
                    node.iAdd(i - 1);
                    final BList<ENTRY_TYPE> left = (BList) node.iGet(i - 1);
                    left.setNodeFactory(bnode.getNode().getFactory());
                    bnode.inodeSplit(left);
                    if (node.size() < nodeCapacity) {
                        return;
                    }
                    if (isRoot) {
                        rootSplit();
                        return;
                    }
                }
                return;
            }
            ndx -= bns;
        }
    }

    protected void rootSplit() throws Exception {
        final SList<ENTRY_TYPE> oldRootNode = getNode();
        final FactoryImpl oldFactory = oldRootNode.getFactory();
        getUnionJid().setValue(1);
        final SList<ENTRY_TYPE> newRootNode = getNode();
        newRootNode.iAdd(0);
        newRootNode.iAdd(1);
        final BList<ENTRY_TYPE> leftBNode = (BList) newRootNode.iGet(0);
        final BList<ENTRY_TYPE> rightBNode = (BList) newRootNode.iGet(1);
        leftBNode.setNodeFactory(oldFactory);
        rightBNode.setNodeFactory(oldFactory);
        final int h = nodeCapacity / 2;
        int i = 0;
        if (oldFactory.name.startsWith("LL.")) {
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
                final BList<ENTRY_TYPE> e = (BList) oldRootNode.iGet(i);
                final int eSize = e.size();
                final byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
            while (i < nodeCapacity) {
                final BList<ENTRY_TYPE> e = (BList) oldRootNode.iGet(i);
                final int eSize = e.size();
                final byte[] bytes = e.getSerializedBytes();
                rightBNode.append(bytes, eSize);
                i += 1;
            }
        }
    }

    protected void inodeSplit(final BList<ENTRY_TYPE> leftBNode)
            throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
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
                final BList<ENTRY_TYPE> e = (BList) node.iGet(0);
                node.iRemove(0);
                final int eSize = e.size();
                incSize(-eSize);
                final byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
        }
    }

    @Override
    public void empty() throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
        node.empty();
        final JAIntegerImpl sj = getSizeJid();
        sj.setValue(0);
    }

    @Override
    public AsyncRequest<Void> iRemoveReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
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
        final SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            node.iRemove(ndx);
            incSize(-1);
            return;
        }
        int i = 0;
        while (i < node.size()) {
            BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            final int bns = bnode.size();
            if (ndx < bns) {
                bnode.iRemove(ndx);
                incSize(-1);
                final int bnodeSize = bnode.size();
                if (bnodeSize > (nodeCapacity / 3)) {
                    return;
                }
                if (bnodeSize == 0) {
                    node.iRemove(ndx);
                } else {
                    if (i > 0) {
                        final BList<ENTRY_TYPE> leftBNode = (BList) node
                                .iGet(i - 1);
                        if ((leftBNode.nodeSize() + bnodeSize) < nodeCapacity) {
                            bnode.append(leftBNode);
                            node.iRemove(i);
                        }
                    }
                    if ((i + 1) < node.size()) {
                        final BList<ENTRY_TYPE> rightBNode = (BList) node
                                .iGet(i + 1);
                        if ((bnodeSize + rightBNode.nodeSize()) < nodeCapacity) {
                            rightBNode.append(bnode);
                            node.iRemove(i + 1);
                        }
                    }
                }
                if ((node.size() == 1) && isRoot && !isLeaf()) {
                    bnode = (BList) node.iGet(0);
                    setNodeFactory(bnode.getNode().getFactory());
                    final JAIntegerImpl sj = getSizeJid();
                    sj.setValue(0);
                    bnode.append(this);
                }
                return;
            }
            ndx -= bns;
            i += 1;
        }
        throw new IllegalArgumentException();
    }

    void append(final BList<ENTRY_TYPE> leftNode) throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
        int i = 0;
        if (isLeaf()) {
            while (i < node.size()) {
                final JASerializable e = node.iGet(i);
                leftNode.append(e.getDurable().getSerializedBytes(), 1);
                i += 1;
            }
        } else {
            while (i < node.size()) {
                final BList<ENTRY_TYPE> e = (BList) node.iGet(i);
                leftNode.append(e.getSerializedBytes(), e.size());
                i += 1;
            }
        }
    }

    void append(final byte[] bytes, final int eSize) throws Exception {
        final SList<ENTRY_TYPE> node = getNode();
        node.iAdd(-1, bytes);
        incSize(eSize);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
