package org.agilewiki.pactor.durable.impl.scalar.vlens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.durable.AppendableBytes;
import org.agilewiki.pactor.durable.Durables;
import org.agilewiki.pactor.durable.ReadableBytes;
import org.agilewiki.pactor.durable.impl.FactoryImpl;
import org.agilewiki.pactor.durable.impl.scalar.Scalar;
import org.agilewiki.pactor.util.Ancestor;

/**
 * A JID component that holds a variable-length value, or null.
 */
abstract public class VLenScalar<SET_TYPE, RESPONSE_TYPE>
        extends Scalar<SET_TYPE, RESPONSE_TYPE> {

    /**
     * Holds the value, or null.
     */
    protected RESPONSE_TYPE value = null;

    /**
     * The size of the serialized (exclusive of its length header).
     */
    protected int len = -1;

    private Request<Void> clearReq;

    public Request<Void> clearReq() {
        return clearReq;
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param v The value.
     * @return True if a new value is created.
     * @throws Exception Any uncaught exception raised.
     */
    abstract public Boolean makeValue(SET_TYPE v)
            throws Exception;

    /**
     * Clear the content.
     *
     * @throws Exception Any uncaught exception raised.
     */
    public void clear() throws Exception {
        if (len == -1)
            return;
        int l = len;
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
    public int getSerializedLength() {
        if (len == -1)
            return Durables.INT_LENGTH;
        return Durables.INT_LENGTH + len;
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the serialized data (exclusive of its length header).
     */
    protected int loadLen(ReadableBytes readableBytes) {
        int l = readableBytes.readInt();
        return l;
    }

    /**
     * Writes the size of the serialized data (exclusive of its length header).
     *
     * @param appendableBytes The object written to.
     */
    protected void saveLen(AppendableBytes appendableBytes) throws Exception {
        appendableBytes.writeInt(len);
    }

    /**
     * Skip over the length at the beginning of the serialized data.
     *
     * @param readableBytes Holds the serialized data.
     */
    protected void skipLen(ReadableBytes readableBytes) {
        readableBytes.skip(Durables.INT_LENGTH);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     * @throws Exception Any uncaught exception which occurred while processing the change.
     */
    @Override
    public void change(int lengthChange) {
        if (len == -1)
            len = lengthChange;
        else
            len += lengthChange;
        super.change(lengthChange);
    }

    /**
     * Assigns the serialized data to the JID.
     *
     * @param readableBytes Holds the immutable serialized data.
     */
    @Override
    public void load(ReadableBytes readableBytes) {
        super.load(readableBytes);
        len = loadLen(readableBytes);
        value = null;
        if (len > -1)
            readableBytes.skip(len);
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        clearReq = new RequestBase<Void>(getMailbox()) {
            public void processRequest(Transport rp) throws Exception {
                clear();
                rp.processResponse(null);
            }
        };
    }
}
