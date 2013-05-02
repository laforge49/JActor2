package org.agilewiki.jactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.incDes.JAString;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with String keys.
 */
public class StringSMap<VALUE_TYPE extends IncDesImpl> extends SMap<String, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    final protected Factory getKeyFactory() {
        return Durables.getFactoryLocator(getMailbox()).getFactory(JAString.FACTORY_NAME);
    }

    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    final protected String stringToKey(String skey) {
        return skey;
    }
}
