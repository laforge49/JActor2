package org.agilewiki.pactor.durable.impl.scalar.vlens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.durable.*;
import org.agilewiki.pactor.durable.impl.FactoryImpl;
import org.agilewiki.pactor.durable.impl.IncDesImpl;
import org.agilewiki.pautil.Ancestor;

/**
 * A JID actor that holds a JID actor.
 */
public class BoxImpl
        extends VLenScalar<String, PASerializable> implements Box {

    public static void registerFactory(FactoryLocator factoryLocator)
            throws Exception {
        factoryLocator.registerFactory(new FactoryImpl(Box.FACTORY_NAME) {
            @Override
            final protected BoxImpl instantiateActor() {
                return new BoxImpl();
            }
        });
    }

    private Request<Void> clearReq;
    private Request<PASerializable> getPAIDReq;

    @Override
    public Request<Void> clearReq() {
        return clearReq;
    }

    @Override
    public Request<PASerializable> getIncDesReq() {
        return getPAIDReq;
    }

    /**
     * Clear the content.
     *
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public void clear() throws Exception {
        if (len == -1)
            return;
        int l = len;
        if (value != null) {
            ((IncDesImpl) value.getDurable()).setContainerJid(null);
            value = null;
        }
        serializedBytes = null;
        serializedOffset = -1;
        change(-l);
        len = -1;
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param jidType The MakeValue request.
     * @return True if a new value is created.
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public Boolean makeValue(final String jidType)
            throws Exception {
        if (len > -1)
            return false;
        setValue(jidType);
        return true;
    }

    @Override
    public Request<Boolean> makeIncDesReq(final String jidType) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(makeValue(jidType));
            }
        };
    }

    /**
     * Assign a value.
     *
     * @param jidType The jid type.
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public void setValue(final String jidType)
            throws Exception {
        value = createSubordinate(jidType);
        int l = Durables.stringLength(((FactoryImpl) value.getDurable().getFactory()).getFactoryKey()) +
                value.getDurable().getSerializedLength();
        change(l);
        serializedBytes = null;
        serializedOffset = -1;
    }

    @Override
    public Request<Void> setIncDesReq(final String actorType) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(actorType);
                rp.processResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType An jid type name.
     * @param bytes   The serialized data.
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public void setValue(final String jidType, final byte[] bytes)
            throws Exception {
        if (len > -1)
            clear();
        setBytes(jidType, bytes);
    }

    @Override
    public Request<Void> setIncDesReq(final String jidType, final byte[] bytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(jidType, bytes);
                rp.processResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data, unless a JID actor is already present.
     *
     * @param jidType An jid type name.
     * @param bytes   The serialized data.
     * @return True if a new value is created.
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public Boolean makeValue(final String jidType, final byte[] bytes)
            throws Exception {
        if (len > -1)
            return false;
        setBytes(jidType, bytes);
        return true;
    }

    @Override
    public Request<Boolean> makeIncDesReq(final String jidType, final byte[] bytes) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(makeValue(jidType, bytes));
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType The jid type.
     * @param bytes   The serialized data.
     * @throws Exception Any uncaught exception raised.
     */
    public void setBytes(String jidType, byte[] bytes)
            throws Exception {
        value = createSubordinate(jidType, bytes);
        int l = Durables.stringLength(((FactoryImpl) value.getDurable().getFactory()).getFactoryKey()) +
                value.getDurable().getSerializedLength();
        change(l);
        serializedBytes = null;
        serializedOffset = -1;
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidFactory The jid factory.
     * @param bytes      The serialized data.
     * @throws Exception Any uncaught exception raised.
     */
    public void setBytes(FactoryImpl jidFactory, byte[] bytes)
            throws Exception {
        value = createSubordinate(jidFactory, bytes);
        int l = Durables.stringLength(jidFactory.getFactoryKey()) +
                value.getDurable().getSerializedLength();
        change(l);
        serializedBytes = null;
        serializedOffset = -1;
    }

    /**
     * Returns the actor held by this component.
     *
     * @return The actor held by this component, or null.
     * @throws Exception Any uncaught exception raised during deserialization.
     */
    @Override
    public PASerializable getValue()
            throws Exception {
        if (len == -1)
            return null;
        if (value != null)
            return value;
        if (len == -1) {
            return null;
        }
        ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        String factoryKey = readableBytes.readString();
        value = createSubordinate(factoryKey, readableBytes);
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
        saveLen(appendableBytes);
        if (len == -1)
            return;
        String factoryKey = ((FactoryImpl) value.getDurable().getFactory()).getFactoryKey();
        appendableBytes.writeString(factoryKey);
        value.getDurable().save(appendableBytes);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     * @throws Exception Any uncaught exception which occurred while processing the request.
     */
    @Override
    public PASerializable resolvePathname(String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        if (pathname.equals("0")) {
            return getValue();
        }
        if (pathname.startsWith("0/")) {
            PASerializable v = getValue();
            if (v == null)
                return null;
            return v.getDurable().resolvePathname(pathname.substring(2));
        }
        throw new IllegalArgumentException("pathname " + pathname);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        clearReq = new RequestBase<Void>(getMailbox()) {
            public void processRequest(Transport rp) throws Exception {
                clear();
                rp.processResponse(null);
            }
        };

        getPAIDReq = new RequestBase<PASerializable>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
