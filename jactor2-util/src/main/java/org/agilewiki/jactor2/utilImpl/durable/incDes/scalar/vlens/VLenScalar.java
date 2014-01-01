package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.Scalar;

/**
 * A JID component that holds a variable-length value, or null.
 */
abstract public class VLenScalar<SET_TYPE, RESPONSE_TYPE> extends
        Scalar<SET_TYPE, RESPONSE_TYPE> {

    /**
     * Holds the value, or null.
     */
    protected RESPONSE_TYPE value = null;

    /**
     * The size of the serialized (exclusive of its length header).
     */
    protected int len = -1;

    public AsyncRequest<Void> clearReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                clear();
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param v The value.
     * @return True if a new value is created.
     */
    abstract public Boolean makeValue(SET_TYPE v) throws Exception;

    /**
     * Clear the content.
     */
    public void clear() {
        if (len == -1) {
            return;
        }
        final int l = len;
        value = null;
        serializedBytes = null;
        serializedOffset = -1;
        change(-l);
        len = -1;
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() throws Exception {
        if (len == -1) {
            return JAInteger.LENGTH;
        }
        return JAInteger.LENGTH + len;
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the serialized data (exclusive of its length header).
     */
    protected int loadLen(final ReadableBytes readableBytes) throws Exception {
        final int l = readableBytes.readInt();
        return l;
    }

    /**
     * Writes the size of the serialized data (exclusive of its length header).
     *
     * @param appendableBytes The object written to.
     */
    protected void saveLen(final AppendableBytes appendableBytes)
            throws Exception {
        appendableBytes.writeInt(len);
    }

    /**
     * Skip over the length at the beginning of the serialized data.
     *
     * @param readableBytes Holds the serialized data.
     */
    protected void skipLen(final ReadableBytes readableBytes) throws Exception {
        readableBytes.skip(JAInteger.LENGTH);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    @Override
    public void change(final int lengthChange) {
        if (len == -1) {
            len = lengthChange;
        } else {
            len += lengthChange;
        }
        super.change(lengthChange);
    }

    /**
     * Assigns the serialized data to the JID.
     *
     * @param readableBytes Holds the immutable serialized data.
     */
    @Override
    public void load(final ReadableBytes readableBytes) throws Exception {
        super.load(readableBytes);
        len = loadLen(readableBytes);
        value = null;
        if (len > -1) {
            readableBytes.skip(len);
        }
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
