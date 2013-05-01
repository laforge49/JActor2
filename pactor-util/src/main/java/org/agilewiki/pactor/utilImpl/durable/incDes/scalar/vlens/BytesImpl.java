package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.Bytes;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A JID component that holds a byte array.
 */
public class BytesImpl
        extends VLenScalar<byte[], byte[]> implements Bytes {

    public static void registerFactory(FactoryLocator factoryLocator) {
        factoryLocator.registerFactory(new FactoryImpl(Bytes.FACTORY_NAME) {
            @Override
            final protected BytesImpl instantiateActor() {
                return new BytesImpl();
            }
        });
    }

    private Request<byte[]> getBytesReq;

    @Override
    public Request<byte[]> getValueReq() {
        return getBytesReq;
    }

    /**
     * Assign a value.
     *
     * @param v The new value.
     */
    @Override
    public void setValue(final byte[] v) {
        int c = v.length;
        if (len > -1)
            c -= len;
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
    }

    @Override
    public Request<Void> setValueReq(final byte[] v) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(v);
                rp.processResponse(null);
            }
        };
    }

    @Override
    public void setObject(Object v) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(v);
        oos.close();
        byte[] bytes = baos.toByteArray();
        setValue(bytes);
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param v The MakeValue request.
     * @return True if a new value is created.
     * @throws Exception Any uncaught exception raised.
     */
    @Override
    public Boolean makeValue(final byte[] v) {
        if (len > -1)
            return false;
        int c = v.length;
        if (len > -1)
            c -= len;
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
        return true;
    }

    @Override
    public Request<Boolean> makeValueReq(final byte[] v) {
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
    public byte[] getValue() {
        if (len == -1)
            return null;
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        value = readableBytes.readBytes(len);
        return value;
    }

    @Override
    public Object getObject() throws Exception {
        byte[] bytes = getValue();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        saveLen(appendableBytes);
        if (len == -1)
            return;
        appendableBytes.writeBytes(value);
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        getBytesReq = new RequestBase<byte[]>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
