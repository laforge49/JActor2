package org.agilewiki.pactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.incDes.PAInteger;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with Integer keys.
 */
public class IntegerSMap<VALUE_TYPE extends IncDesImpl> extends SMap<Integer, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    final protected Factory getKeyFactory() {
        return Durables.getFactory(Durables.getFactoryLocator(getMailbox()), PAInteger.FACTORY_NAME);
    }

    /**
     * Converts a string to a key.
     *
     * @param skey The integer to be converted.
     * @return The key.
     */
    final protected Integer stringToKey(String skey) {
        return new Integer(skey);
    }
}
