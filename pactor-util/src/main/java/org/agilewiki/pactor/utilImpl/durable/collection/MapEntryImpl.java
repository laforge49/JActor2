package org.agilewiki.pactor.utilImpl.durable.collection;

import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.MapEntry;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.utilImpl.durable.DurableImpl;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.IncDesImpl;
import org.agilewiki.pactor.utilImpl.durable.scalar.Scalar;

/**
 * A map is, in part, a list of map entries.
 */
public class MapEntryImpl<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends DurableImpl
        implements MapEntry<KEY_TYPE, VALUE_TYPE> {

    private final static int TUPLE_KEY = 0;
    private final static int TUPLE_VALUE = 1;

    void setFactories(Factory keyFactory, Factory valueFactory) {
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_KEY] = keyFactory;
        tupleFactories[TUPLE_VALUE] = valueFactory;
    }

    @Override
    public KEY_TYPE getKey() {
        return (KEY_TYPE) ((Scalar) _iGet(TUPLE_KEY).getDurable()).getValue();
    }

    protected void setKey(KEY_TYPE key) {
        ((Scalar) _iGet(TUPLE_KEY).getDurable()).setValue(key);
    }

    @Override
    public VALUE_TYPE getValue() {
        return (VALUE_TYPE) _iGet(TUPLE_VALUE);
    }

    public void setValueBytes(byte[] bytes) {
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
    public int compareKeyTo(KEY_TYPE o) {
        return getKey().compareTo(o);
    }
}
