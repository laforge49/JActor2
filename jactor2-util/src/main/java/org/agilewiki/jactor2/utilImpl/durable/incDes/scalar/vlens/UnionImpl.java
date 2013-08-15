package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.Mailbox;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.*;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.Union;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.Scalar;

public class UnionImpl extends Scalar<String, JASerializable> implements Union {

    public static void registerFactory(final FactoryLocator _factoryLocator,
                                       final String _subActorType,
                                       final String... _actorTypes) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(_subActorType) {

            @Override
            protected UnionImpl instantiateActor() {
                return new UnionImpl();
            }

            @Override
            public UnionImpl newSerializable(Mailbox mailbox, Ancestor parent)
                    throws Exception {
                UnionImpl uj = (UnionImpl) super.newSerializable(mailbox, parent);
                Factory[] afs = new FactoryImpl[_actorTypes.length];
                int i = 0;
                while (i < _actorTypes.length) {
                    afs[i] = _factoryLocator.getFactory(_actorTypes[i]);
                    i += 1;
                }
                uj.unionFactories = afs;
                return uj;
            }
        });
    }

    protected Factory[] unionFactories;
    protected int factoryIndex = -1;
    protected JASerializable value;

    private Request<Void> clearReq;
    private Request<JASerializable> getPAIDReq;

    public Request<Void> clearReq() {
        return clearReq;
    }

    @Override
    public Request<JASerializable> getValueReq() {
        return getPAIDReq;
    }

    protected Factory[] getUnionFactories() {
        if (unionFactories != null)
            return unionFactories;
        throw new IllegalStateException("unionFactories is null");
    }

    protected int getFactoryIndex(String actorType) throws Exception {
        FactoryLocator factoryLocator = Durables.getFactoryLocator(getMailbox());
        Factory actorFactory = factoryLocator.getFactory(actorType);
        return getFactoryIndex(actorFactory);
    }

    protected int getFactoryIndex(Factory actorFactory) {
        String factoryKey = ((FactoryImpl) actorFactory).getFactoryKey();
        Factory[] uf = getUnionFactories();
        int i = 0;
        while (i < uf.length) {
            if (((FactoryImpl) uf[i]).getFactoryKey().equals(factoryKey))
                return i;
            i += 1;
        }
        throw new IllegalArgumentException("Not a valid union type: " + factoryKey);
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(ReadableBytes readableBytes)
            throws Exception {
        super.load(readableBytes);
        factoryIndex = readableBytes.readInt();
        if (factoryIndex == -1)
            return;
        Factory factory = getUnionFactories()[factoryIndex];
        value = factory.newSerializable(getMailbox(), getParent());
        ((IncDesImpl) value.getDurable()).load(readableBytes);
        ((IncDesImpl) value.getDurable()).setContainerJid(this);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength()
            throws Exception {
        if (factoryIndex == -1)
            return JAInteger.LENGTH;
        return JAInteger.LENGTH + value.getDurable().getSerializedLength();
    }

    /**
     * Clear the content.
     */
    @Override
    public void clear()
            throws Exception {
        setValue(-1);
    }

    @Override
    public void setValue(final String actorType)
            throws Exception {
        setValue(getFactoryIndex(actorType));
    }

    @Override
    public Request<Void> setValueReq(final String actorType) {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(actorType);
                rp.processResponse(null);
            }
        };
    }

    public void setValue(final FactoryImpl factoryImpl)
            throws Exception {
        setValue(getFactoryIndex(factoryImpl));
    }

    public void setValue(Integer ndx)
            throws Exception {
        int oldLength = getSerializedLength();
        if (value != null)
            ((IncDesImpl) value.getDurable()).setContainerJid(null);
        if (ndx == -1) {
            factoryIndex = -1;
            value = null;
        } else {
            Factory factory = getUnionFactories()[ndx];
            factoryIndex = ndx;
            value = factory.newSerializable(getMailbox(), getParent());
            ((IncDesImpl) value.getDurable()).setContainerJid(this);
        }
        change(getSerializedLength() - oldLength);
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType A jid type name.
     * @param bytes   The serialized data.
     */
    @Override
    public void setValue(final String jidType, final byte[] bytes)
            throws Exception {
        setUnionBytes(getFactoryIndex(jidType), bytes);
    }

    @Override
    public Request<Void> setValueReq(final String jidType, final byte[] bytes) {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(jidType, bytes);
                rp.processResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param ndx   The factory index.
     * @param bytes The serialized data.
     */
    public void setUnionBytes(Integer ndx, byte[] bytes)
            throws Exception {
        int oldLength = getSerializedLength();
        if (value != null)
            ((IncDesImpl) value.getDurable()).setContainerJid(null);
        Factory factory = getUnionFactories()[ndx];
        factoryIndex = ndx;
        value = factory.newSerializable(getMailbox(), getParent());
        ((IncDesImpl) value.getDurable()).setContainerJid(this);
        ((IncDesImpl) value.getDurable()).load(new ReadableBytes(bytes, 0));
        change(getSerializedLength() - oldLength);
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param jidType The MakeValue request.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final String jidType)
            throws Exception {
        return makeUnionValue(getFactoryIndex(jidType));
    }

    @Override
    public Request<Boolean> makeValueReq(final String jidType) {
        return new Request<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(makeValue(jidType));
            }
        };
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param ndx The Make request.
     * @return True if a new value is created.
     */
    public Boolean makeUnionValue(Integer ndx)
            throws Exception {
        if (factoryIndex > -1)
            return false;
        setValue(ndx);
        return true;
    }

    /**
     * Creates a JID actor and loads its serialized data, unless a JID actor is already present.
     *
     * @param jidType A jid type name.
     * @param bytes   The serialized data.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final String jidType, final byte[] bytes)
            throws Exception {
        return makeUnionBytes(getFactoryIndex(jidType), bytes);
    }

    @Override
    public Request<Boolean> makeValueReq(final String jidType, final byte[] bytes) {
        return new Request<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(makeValue(jidType, bytes));
            }
        };
    }

    public Boolean makeUnionBytes(Integer ndx, byte[] bytes)
            throws Exception {
        if (factoryIndex > -1)
            return false;
        setUnionBytes(ndx, bytes);
        return true;
    }

    @Override
    public JASerializable getValue() {
        return value;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
        appendableBytes.writeInt(factoryIndex);
        if (factoryIndex == -1)
            return;
        ((IncDesImpl) value.getDurable()).save(appendableBytes);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public JASerializable resolvePathname(String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        if (pathname.equals("0")) {
            return getValue();
        }
        if (pathname.startsWith("0/")) {
            JASerializable v = getValue();
            if (v == null)
                return null;
            return v.getDurable().resolvePathname(pathname.substring(2));
        }
        throw new IllegalArgumentException("pathname " + pathname);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(mailbox, parent, factory);
        clearReq = new Request<Void>(getMailbox()) {
            public void processRequest(Transport rp) throws Exception {
                clear();
                rp.processResponse(null);
            }
        };

        getPAIDReq = new Request<JASerializable>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
