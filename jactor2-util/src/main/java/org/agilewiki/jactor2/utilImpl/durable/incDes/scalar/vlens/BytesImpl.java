package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.Bytes;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A JID component that holds a byte array.
 */
public class BytesImpl extends VLenScalar<byte[], byte[]> implements Bytes {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                Bytes.FACTORY_NAME) {
            @Override
            final protected BytesImpl instantiateBlade() {
                return new BytesImpl();
            }
        });
    }

    @Override
    public AsyncRequest<byte[]> getValueReq() {
        return new AsyncBladeRequest<byte[]>() {
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
    public void setValue(final byte[] v) {
        int c = v.length;
        if (len > -1) {
            c -= len;
        }
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(c);
    }

    @Override
    public AsyncRequest<Void> setValueReq(final byte[] v) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setValue(v);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void setObject(final Object v) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(v);
        oos.close();
        final byte[] bytes = baos.toByteArray();
        setValue(bytes);
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param v The MakeValue request.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final byte[] v) {
        if (len > -1) {
            return false;
        }
        int c = v.length;
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
    public AsyncRequest<Boolean> makeValueReq(final byte[] v) {
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
    public byte[] getValue() throws Exception {
        if (len == -1) {
            return null;
        }
        if (value != null) {
            return value;
        }
        final ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        value = readableBytes.readBytes(len);
        return value;
    }

    @Override
    public Object getObject() throws Exception {
        final byte[] bytes = getValue();
        if (bytes == null) {
            return null;
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = new ObjectInputStream(bais);
        final Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(final AppendableBytes appendableBytes)
            throws Exception {
        saveLen(appendableBytes);
        if (len == -1) {
            return;
        }
        appendableBytes.writeBytes(value);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
