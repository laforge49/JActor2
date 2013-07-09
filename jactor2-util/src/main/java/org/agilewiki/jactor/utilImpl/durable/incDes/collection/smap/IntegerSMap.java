package org.agilewiki.jactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.incDes.JAInteger;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with Integer keys.
 */
public class IntegerSMap<VALUE_TYPE extends IncDesImpl> extends SMap<Integer, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    final protected Factory getKeyFactory() throws Exception {
        return Durables.getFactoryLocator(getMailbox()).getFactory(JAInteger.FACTORY_NAME);
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
