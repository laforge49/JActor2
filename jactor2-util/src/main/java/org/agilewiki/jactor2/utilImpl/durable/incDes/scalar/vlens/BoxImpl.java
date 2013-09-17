package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.Box;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * A JID actor that holds a JID actor.
 */
public class BoxImpl
        extends VLenScalar<String, JASerializable> implements Box {

    public static void registerFactory(FactoryLocator _factoryLocator) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(Box.FACTORY_NAME) {
            @Override
            final protected BoxImpl instantiateBlade() {
                return new BoxImpl();
            }
        });
    }

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _length The number of characters in the string.
     * @return The size in bytes.
     */
    public final static int stringLength(final int _length) {
        if (_length == -1)
            return JAInteger.LENGTH;
        if (_length > -1)
            return JAInteger.LENGTH + 2 * _length;
        throw new IllegalArgumentException("invalid string length: " + _length);
    }

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _s The string.
     * @return The size in bytes.
     */
    public final static int stringLength(final String _s) {
        if (_s == null)
            return JAInteger.LENGTH;
        return stringLength(_s.length());
    }

    @Override
    public AsyncRequest<Void> clearReq() {
        return new AsyncBladeRequest<Void>() {
            protected void processAsyncRequest() throws Exception {
                clear();
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public AsyncRequest<JASerializable> getValueReq() {
        return new AsyncBladeRequest<JASerializable>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(getValue());
            }
        };
    }

    /**
     * Clear the content.
     */
    @Override
    public void clear() {
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
    public AsyncRequest<Boolean> makeValueReq(final String jidType) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(makeValue(jidType));
            }
        };
    }

    /**
     * Assign a value.
     *
     * @param jidType The jid type.
     */
    @Override
    public void setValue(final String jidType)
            throws Exception {
        value = createSubordinate(jidType);
        int l = stringLength(((FactoryImpl) value.getDurable().getFactory()).getFactoryKey()) +
                value.getDurable().getSerializedLength();
        change(l);
        serializedBytes = null;
        serializedOffset = -1;
    }

    @Override
    public AsyncRequest<Void> setValueReq(final String actorType) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setValue(actorType);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType An jid type name.
     * @param bytes   The serialized data.
     */
    @Override
    public void setValue(final String jidType, final byte[] bytes)
            throws Exception {
        if (len > -1)
            clear();
        setBytes(jidType, bytes);
    }

    @Override
    public AsyncRequest<Void> setValueReq(final String jidType, final byte[] bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setValue(jidType, bytes);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data, unless a JID actor is already present.
     *
     * @param jidType An jid type name.
     * @param bytes   The serialized data.
     * @return True if a new value is created.
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
    public AsyncRequest<Boolean> makeValueReq(final String jidType, final byte[] bytes) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(makeValue(jidType, bytes));
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType The jid type.
     * @param bytes   The serialized data.
     */
    public void setBytes(String jidType, byte[] bytes)
            throws Exception {
        value = createSubordinate(jidType, bytes);
        int l = stringLength(((FactoryImpl) value.getDurable().getFactory()).getFactoryKey()) +
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
     */
    public void setBytes(FactoryImpl jidFactory, byte[] bytes)
            throws Exception {
        value = createSubordinate(jidFactory, bytes);
        int l = stringLength(jidFactory.getFactoryKey()) +
                value.getDurable().getSerializedLength();
        change(l);
        serializedBytes = null;
        serializedOffset = -1;
    }

    /**
     * Returns the actor held by this component.
     *
     * @return The actor held by this component, or null.
     */
    @Override
    public JASerializable getValue()
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

    public void initialize(final Reactor reactor, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
