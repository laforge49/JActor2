package org.agilewiki.pactor.utilImpl.durable.collection.blist;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.*;
import org.agilewiki.pactor.utilImpl.durable.DurableImpl;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.collection.slist.SList;
import org.agilewiki.pactor.utilImpl.durable.scalar.flens.PAIntegerImpl;
import org.agilewiki.pactor.utilImpl.durable.scalar.vlens.UnionImpl;

/**
 * A balanced tree holding a list of JIDs, all of the same type.
 */
public class BList<ENTRY_TYPE extends PASerializable>
        extends DurableImpl
        implements PAList<ENTRY_TYPE>, Collection<ENTRY_TYPE> {
    protected final int TUPLE_SIZE = 0;
    protected final int TUPLE_UNION = 1;
    protected int nodeCapacity = 28;
    protected boolean isRoot;
    protected Factory entryFactory;
    protected FactoryLocator factoryLocator;

    private Request<Integer> sizeReq;
    private Request<Void> emptyReq;

    @Override
    public Request<Integer> sizeReq() {
        return sizeReq;
    }

    public Request<Void> emptyReq() {
        return emptyReq;
    }

    /**
     * Returns the IncDesFactory for all the elements in the list.
     *
     * @return The IncDesFactory for of all the elements in the list.
     */
    protected Factory getEntryFactory() {
        if (entryFactory == null)
            throw new IllegalStateException("entryFactory uninitialized");
        return entryFactory;
    }

    protected void init() {
        String baseType = getType();
        if (baseType.startsWith("IN."))
            baseType = baseType.substring(3);
        factoryLocator = Durables.getFactoryLocator(getMailbox());
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_SIZE] = factoryLocator.getFactory(PAInteger.FACTORY_NAME);
        tupleFactories[TUPLE_UNION] = factoryLocator.getFactory("U." + baseType);
    }

    protected void setNodeLeaf() {
        getUnionJid().setValue(0);
    }

    protected void setNodeFactory(FactoryImpl factoryImpl) {
        getUnionJid().setValue(factoryImpl);
    }

    protected PAIntegerImpl getSizeJid() {
        return (PAIntegerImpl) _iGet(TUPLE_SIZE);
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size() {
        return getSizeJid().getValue();
    }

    protected void incSize(int inc) {
        PAIntegerImpl sj = getSizeJid();
        sj.setValue(sj.getValue() + inc);
    }

    protected UnionImpl getUnionJid() {
        return (UnionImpl) _iGet(TUPLE_UNION);
    }

    protected SList<ENTRY_TYPE> getNode() {
        return (SList) getUnionJid().getValue();
    }

    public String getNodeFactoryKey() {
        return getNode().getFactory().getFactoryKey();
    }

    public boolean isLeaf() {
        return getNodeFactoryKey().startsWith("LL.");
    }

    public int nodeSize() {
        return getNode().size();
    }

    public boolean isFat() {
        return nodeSize() >= nodeCapacity;
    }

    @Override
    public Request<ENTRY_TYPE> iGetReq(final int _i) {
        return new RequestBase<ENTRY_TYPE>(getMailbox()) {
            @Override
            public void processRequest(Transport<ENTRY_TYPE> _rp) throws Exception {
                _rp.processResponse(iGet(_i));
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
    public ENTRY_TYPE iGet(int ndx) {
        SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            return (ENTRY_TYPE) node.iGet(ndx);
        }
        if (ndx < 0)
            ndx += size();
        if (ndx < 0 || ndx >= size())
            return null;
        int i = 0;
        while (i < node.size()) {
            BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
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
    public Request<Void> iSetReq(final int _i, final byte[] _bytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport _rp) throws Exception {
                iSet(_i, _bytes);
                _rp.processResponse(null);
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
        SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            node.iSet(ndx, bytes);
            return;
        }
        if (ndx < 0)
            ndx += size();
        if (ndx < 0 || ndx >= size())
            throw new IllegalArgumentException();
        int i = 0;
        while (i < node.size()) {
            BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            int bns = bnode.size();
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
    public PASerializable resolvePathname(String pathname) {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        int s = pathname.indexOf("/");
        if (s == -1)
            s = pathname.length();
        if (s == 0)
            throw new IllegalArgumentException("pathname " + pathname);
        String ns = pathname.substring(0, s);
        int n = 0;
        try {
            n = Integer.parseInt(ns);
        } catch (Exception ex) {
            throw new IllegalArgumentException("pathname " + pathname);
        }
        if (n < 0 || n >= size())
            throw new IllegalArgumentException("pathname " + pathname);
        PASerializable jid = iGet(n);
        if (s == pathname.length())
            return jid;
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    @Override
    public Request<Void> iAddReq(final int _i) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _rp) throws Exception {
                iAdd(_i);
                _rp.processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i) {
        iAdd(i, null);
    }

    @Override
    public Request<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _rp) throws Exception {
                iAdd(_i, _bytes);
                _rp.processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int ndx, byte[] bytes) {
        if (ndx < 0)
            ndx = size() + 1 + ndx;
        if (ndx < 0 || ndx > size())
            throw new IllegalArgumentException();
        incSize(1);
        SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            if (bytes == null)
                node.iAdd(ndx);
            else
                node.iAdd(ndx, bytes);
            if (node.size() < nodeCapacity)
                return;
            if (isRoot) {
                rootSplit();
                return;
            }
            return;
        }
        int i = 0;
        while (true) {
            BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            int bns = bnode.size();
            i += 1;
            if (ndx < bns || i == node.size()) {
                bnode.iAdd(ndx, bytes);
                if (bnode.isFat()) {
                    node.iAdd(i - 1);
                    BList<ENTRY_TYPE> left = (BList) node.iGet(i - 1);
                    left.setNodeFactory(bnode.getNode().getFactory());
                    bnode.inodeSplit(left);
                    if (node.size() < nodeCapacity)
                        return;
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

    protected void rootSplit() {
        SList<ENTRY_TYPE> oldRootNode = getNode();
        FactoryImpl oldFactory = oldRootNode.getFactory();
        getUnionJid().setValue(1);
        SList<ENTRY_TYPE> newRootNode = getNode();
        newRootNode.iAdd(0);
        newRootNode.iAdd(1);
        BList<ENTRY_TYPE> leftBNode = (BList) newRootNode.iGet(0);
        BList<ENTRY_TYPE> rightBNode = (BList) newRootNode.iGet(1);
        leftBNode.setNodeFactory(oldFactory);
        rightBNode.setNodeFactory(oldFactory);
        int h = nodeCapacity / 2;
        int i = 0;
        if (oldFactory.name.startsWith("LL.")) {
            while (i < h) {
                PASerializable e = oldRootNode.iGet(i);
                byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            while (i < nodeCapacity) {
                PASerializable e = oldRootNode.iGet(i);
                byte[] bytes = e.getDurable().getSerializedBytes();
                rightBNode.iAdd(-1, bytes);
                i += 1;
            }
        } else {
            while (i < h) {
                BList<ENTRY_TYPE> e = (BList) oldRootNode.iGet(i);
                int eSize = e.size();
                byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
            while (i < nodeCapacity) {
                BList<ENTRY_TYPE> e = (BList) oldRootNode.iGet(i);
                int eSize = e.size();
                byte[] bytes = e.getSerializedBytes();
                rightBNode.append(bytes, eSize);
                i += 1;
            }
        }
    }

    protected void inodeSplit(BList<ENTRY_TYPE> leftBNode) {
        SList<ENTRY_TYPE> node = getNode();
        int h = nodeCapacity / 2;
        int i = 0;
        if (isLeaf()) {
            while (i < h) {
                PASerializable e = node.iGet(0);
                node.iRemove(0);
                byte[] bytes = e.getDurable().getSerializedBytes();
                leftBNode.iAdd(-1, bytes);
                i += 1;
            }
            incSize(-h);
        } else {
            while (i < h) {
                BList<ENTRY_TYPE> e = (BList) node.iGet(0);
                node.iRemove(0);
                int eSize = e.size();
                incSize(-eSize);
                byte[] bytes = e.getSerializedBytes();
                leftBNode.append(bytes, eSize);
                i += 1;
            }
        }
    }

    @Override
    public void empty() {
        SList<ENTRY_TYPE> node = getNode();
        node.empty();
        PAIntegerImpl sj = getSizeJid();
        sj.setValue(0);
    }

    @Override
    public Request<Void> iRemoveReq(final int _i) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _rp) throws Exception {
                iRemove(_i);
                _rp.processResponse(null);
            }
        };
    }

    @Override
    public void iRemove(int ndx) {
        int s = size();
        if (ndx < 0)
            ndx += s;
        if (ndx < 0 || ndx >= s)
            throw new IllegalArgumentException();
        SList<ENTRY_TYPE> node = getNode();
        if (isLeaf()) {
            node.iRemove(ndx);
            incSize(-1);
            return;
        }
        int i = 0;
        while (i < node.size()) {
            BList<ENTRY_TYPE> bnode = (BList) node.iGet(i);
            int bns = bnode.size();
            if (ndx < bns) {
                bnode.iRemove(ndx);
                incSize(-1);
                int bnodeSize = bnode.size();
                if (bnodeSize > nodeCapacity / 3)
                    return;
                if (bnodeSize == 0) {
                    node.iRemove(ndx);
                } else {
                    if (i > 0) {
                        BList<ENTRY_TYPE> leftBNode = (BList) node.iGet(i - 1);
                        if (leftBNode.nodeSize() + bnodeSize < nodeCapacity) {
                            bnode.append(leftBNode);
                            node.iRemove(i);
                        }
                    }
                    if (i + 1 < node.size()) {
                        BList<ENTRY_TYPE> rightBNode = (BList) node.iGet(i + 1);
                        if (bnodeSize + rightBNode.nodeSize() < nodeCapacity) {
                            rightBNode.append(bnode);
                            node.iRemove(i + 1);
                        }
                    }
                }
                if (node.size() == 1 && isRoot && !isLeaf()) {
                    bnode = (BList) node.iGet(0);
                    setNodeFactory(bnode.getNode().getFactory());
                    PAIntegerImpl sj = getSizeJid();
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

    void append(BList<ENTRY_TYPE> leftNode) {
        SList<ENTRY_TYPE> node = getNode();
        int i = 0;
        if (isLeaf()) {
            while (i < node.size()) {
                PASerializable e = node.iGet(i);
                leftNode.append(e.getDurable().getSerializedBytes(), 1);
                i += 1;
            }
        } else {
            while (i < node.size()) {
                BList<ENTRY_TYPE> e = (BList) node.iGet(i);
                leftNode.append(e.getSerializedBytes(), e.size());
                i += 1;
            }
        }
    }

    void append(byte[] bytes, int eSize) {
        SList<ENTRY_TYPE> node = getNode();
        node.iAdd(-1, bytes);
        incSize(eSize);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        sizeReq = new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport<Integer> _rp) throws Exception {
                _rp.processResponse(size());
            }
        };
        emptyReq = new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _rp) throws Exception {
                empty();
                _rp.processResponse(null);
            }
        };
    }
}
