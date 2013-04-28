package org.agilewiki.pactor.utilImpl.durable.collection.tuple;

import org.agilewiki.pactor.util.durable.*;
import org.agilewiki.pactor.utilImpl.durable.IncDesImpl;
import org.agilewiki.pactor.utilImpl.durable.collection.CollectionImpl;

/**
 * Holds a fixed-size array of JID actors of various types.
 */
public class TupleImpl
        extends CollectionImpl<PASerializable>
        implements ComparableKey<Object>, Tuple {
    /**
     * An array of jid factories, one for each element in the tuple.
     */
    protected Factory[] tupleFactories;

    /**
     * A tuple of actors.
     */
    protected PASerializable[] tuple;

    /**
     * Perform lazy initialization.
     *
     */
    private void initializeTuple() {
        if (tuple != null)
            return;
        tupleFactories = getTupleFactories();
        ReadableBytes readableBytes = null;
        if (isSerialized()) {
            readableBytes = readable();
            skipLen(readableBytes);
        }
        tuple = new PASerializable[size()];
        int i = 0;
        len = 0;
        while (i < size()) {
            PASerializable elementJid = createSubordinate(tupleFactories[i], readableBytes);
            len += elementJid.getDurable().getSerializedLength();
            tuple[i] = elementJid;
            i += 1;
        }
    }

    /**
     * Returns the element factories.
     *
     * @return An array of element factories.
     */
    protected Factory[] getTupleFactories() {
        if (tupleFactories != null)
            return tupleFactories;
        throw new IllegalStateException("tupleFactories is null");
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param i     The index of the desired element.
     * @param bytes Holds the serialized data.
     */
    @Override
    public void iSet(int i, byte[] bytes) {
        initializeTuple();
        PASerializable elementJid = createSubordinate(tupleFactories[i], bytes);
        PASerializable oldElementJid = iGet(i);
        ((IncDesImpl) oldElementJid.getDurable()).setContainerJid(null);
        tuple[i] = elementJid;
        change(elementJid.getDurable().getSerializedLength() -
                oldElementJid.getDurable().getSerializedLength());
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() {
        initializeTuple();
        return Durables.INT_LENGTH + len;
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size() {
        return getTupleFactories().length;
    }

    /**
     * Returns the ith JID component.
     *
     * @param i The index of the element of interest.
     * @return The ith JID component, or null if the index is out of range.
     */
    @Override
    public PASerializable iGet(int i) {
        initializeTuple();
        if (i < 0)
            i += size();
        if (i < 0 || i >= size())
            return null;
        return tuple[i];
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        saveLen(appendableBytes);
        int i = 0;
        while (i < size()) {
            iGet(i).getDurable().save(appendableBytes);
            i += 1;
        }
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(ReadableBytes readableBytes) {
        super.load(readableBytes);
        len = loadLen(readableBytes);
        tuple = null;
        readableBytes.skip(len);
    }

    /**
     * Compares element 0
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o) using element 0.
     */
    @Override
    public int compareKeyTo(Object o) {
        ComparableKey<Object> e0 = (ComparableKey<Object>) iGet(0);
        return e0.compareKeyTo(o);
    }
}
