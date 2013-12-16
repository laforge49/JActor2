package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JAString;
import org.agilewiki.jactor2.utilImpl.durable.*;

/**
 * A JID actor that holds a String.
 */
public class JAStringImpl extends VLenScalar<String, String> implements
        ComparableKey<String>, JAString {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                JAString.FACTORY_NAME) {
            @Override
            final protected JAStringImpl instantiateBlade() {
                return new JAStringImpl();
            }
        });
    }

    @Override
    public AsyncRequest<String> getValueReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(getValue());
            }
        };
    }

    /**
     * Assign a value.
     *
     * @param v The new value.
     */
    @Override
    public void setValue(final String v) {
        int c = v.length() * 2;
        if (len > -1) {
            c -= len;
        }
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
    }

    @Override
    public AsyncRequest<Void> setValueReq(final String v) {
        if (v == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setValue(v);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param v The MakeValue request.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final String v) {
        if (len > -1) {
            return false;
        }
        int c = v.length() * 2;
        if (len > -1) {
            c -= len;
        }
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
        return true;
    }

    @Override
    public AsyncRequest<Boolean> makeValueReq(final String v) {
        if (v == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        return new AsyncBladeRequest<Boolean>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(makeValue(v));
            }
        };
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component, or null.
     */
    @Override
    public String getValue() throws Exception {
        if (len == -1) {
            return null;
        }
        if (value != null) {
            return value;
        }
        final ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        value = readableBytes.readString(len);
        return value;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(final AppendableBytes appendableBytes)
            throws Exception {
        if (len == -1) {
            saveLen(appendableBytes);
        } else {
            appendableBytes.writeString(value);
        }
    }

    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    @Override
    public int compareKeyTo(final String o) throws Exception {
        return getValue().compareTo(o);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
