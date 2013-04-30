package org.agilewiki.pactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.incDes.PAString;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesImpl;

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
        return Durables.getFactory(Durables.getFactoryLocator(getMailbox()), PAString.FACTORY_NAME);
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
