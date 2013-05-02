package org.agilewiki.jactor.utilImpl.durable.incDes.collection;

import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.PASerializable;
import org.agilewiki.jactor.util.durable.incDes.MapEntry;
import org.agilewiki.jactor.utilImpl.durable.ComparableKey;
import org.agilewiki.jactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor.utilImpl.durable.app.DurableImpl;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor.utilImpl.durable.incDes.scalar.Scalar;

/**
 * A map is, in part, a list of map entries.
 */
public class MapEntryImpl<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends DurableImpl
        implements MapEntry<KEY_TYPE, VALUE_TYPE>, ComparableKey<KEY_TYPE> {

    private final static int TUPLE_KEY = 0;
    private final static int TUPLE_VALUE = 1;

    void setFactories(Factory keyFactory, Factory valueFactory) {
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_KEY] = keyFactory;
        tupleFactories[TUPLE_VALUE] = valueFactory;
    }

    @Override
    public KEY_TYPE getKey()
            throws Exception {
        return (KEY_TYPE) ((Scalar) _iGet(TUPLE_KEY).getDurable()).getValue();
    }

    public void setKey(KEY_TYPE key)
            throws Exception {
        ((Scalar) _iGet(TUPLE_KEY).getDurable()).setValue(key);
    }

    @Override
    public VALUE_TYPE getValue()
            throws Exception {
        return (VALUE_TYPE) _iGet(TUPLE_VALUE);
    }

    public void setValueBytes(byte[] bytes)
            throws Exception {
        PASerializable old = (PASerializable) getValue();
        ((IncDesImpl) old.getDurable()).setContainerJid(null);
        PASerializable elementJid = createSubordinate(tupleFactories[TUPLE_VALUE], this, bytes);
        tuple[TUPLE_VALUE] = elementJid;
        change(elementJid.getDurable().getSerializedLength() -
                old.getDurable().getSerializedLength());
    }

    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    @Override
    public int compareKeyTo(KEY_TYPE o)
            throws Exception {
        return getKey().compareTo(o);
    }
}
