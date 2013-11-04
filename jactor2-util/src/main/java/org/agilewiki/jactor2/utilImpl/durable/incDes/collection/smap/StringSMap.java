package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.incDes.JAString;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with String keys.
 */
public class StringSMap<VALUE_TYPE extends IncDesImpl> extends
        SMap<String, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    @Override
    final protected Factory getKeyFactory() throws Exception {
        return Durables.getFactoryLocator(getReactor()).getFactory(
                JAString.FACTORY_NAME);
    }

    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    @Override
    final protected String stringToKey(final String skey) {
        return skey;
    }
}
