package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor2.utilImpl.durable.ComparableKey;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.Scalar;

/**
 * A JID actor that holds a fixed-length value.
 * The value is always present.
 */
abstract public class FLenScalar<RESPONSE_TYPE extends Comparable> extends
        Scalar<RESPONSE_TYPE, RESPONSE_TYPE> implements
        ComparableKey<RESPONSE_TYPE> {

    /**
     * The value.
     */
    protected RESPONSE_TYPE value = newValue();

    /**
     * Create the value.
     *
     * @return The default value
     */
    abstract protected RESPONSE_TYPE newValue();

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(final ReadableBytes readableBytes) throws Exception {
        super.load(readableBytes);
        readableBytes.skip(getSerializedLength());
        value = null;
    }

    /**
     * Assign a value.
     *
     * @param v The new value.
     */
    @Override
    public void setValue(final RESPONSE_TYPE v) {
        if (v.equals(value)) {
            return;
        }
        value = v;
        serializedBytes = null;
        serializedOffset = -1;
        change(0);
    }

    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    @Override
    public int compareKeyTo(final RESPONSE_TYPE o) throws Exception {
        return getValue().compareTo(o);
    }
}
