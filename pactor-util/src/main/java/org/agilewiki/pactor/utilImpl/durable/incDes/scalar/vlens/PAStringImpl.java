package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.PAString;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.ComparableKey;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

/**
 * A JID actor that holds a String.
 */
public class PAStringImpl
        extends VLenScalar<String, String>
        implements ComparableKey<String>, PAString {

    public static void registerFactory(FactoryLocator factoryLocator) {
        factoryLocator.registerFactory(new FactoryImpl(PAString.FACTORY_NAME) {
            @Override
            final protected PAStringImpl instantiateActor() {
                return new PAStringImpl();
            }
        });
    }

    private Request<String> getStringReq;

    public Request<String> getValueReq() {
        return getStringReq;
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

    public Request<Void> setValueReq(final String v) {
        if (v == null)
            throw new IllegalArgumentException("value may not be null");
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(v);
                rp.processResponse(null);
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

    public Request<Boolean> makeValueReq(final String v) {
        if (v == null)
            throw new IllegalArgumentException("value may not be null");
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(makeValue(v));
            }
        };
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component, or null.
     */
    @Override
    public String getValue() {
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
    protected void serialize(AppendableBytes appendableBytes) {
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
    public int compareKeyTo(String o) {
        return getValue().compareTo(o);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        getStringReq = new RequestBase<String>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
