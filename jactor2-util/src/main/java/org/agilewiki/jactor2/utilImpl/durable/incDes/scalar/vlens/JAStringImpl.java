package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JAString;
import org.agilewiki.jactor2.utilImpl.durable.*;

/**
 * A JID actor that holds a String.
 */
public class JAStringImpl
        extends VLenScalar<String, String>
        implements ComparableKey<String>, JAString {

    public static void registerFactory(FactoryLocator _factoryLocator) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(JAString.FACTORY_NAME) {
            @Override
            final protected JAStringImpl instantiateActor() {
                return new JAStringImpl();
            }
        });
    }

    public AsyncRequest<String> getValueReq() {
        return new AsyncRequest<String>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
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
        if (len > -1)
            c -= len;
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
    }

    public AsyncRequest<Void> setValueReq(final String v) {
        if (v == null)
            throw new IllegalArgumentException("value may not be null");
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
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
    public Boolean makeValue(String v) {
        if (len > -1)
            return false;
        int c = v.length() * 2;
        if (len > -1)
            c -= len;
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
        return true;
    }

    public AsyncRequest<Boolean> makeValueReq(final String v) {
        if (v == null)
            throw new IllegalArgumentException("value may not be null");
        return new AsyncRequest<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
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
    public String getValue()
            throws Exception {
        if (len == -1)
            return null;
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
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
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
        if (len == -1)
            saveLen(appendableBytes);
        else
            appendableBytes.writeString(value);
    }

    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    @Override
    public int compareKeyTo(String o)
            throws Exception {
        return getValue().compareTo(o);
    }

    public void initialize(final MessageProcessor messageProcessor, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(messageProcessor, parent, factory);
    }
}
