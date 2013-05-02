package org.agilewiki.jactor.utilImpl.durable.incDes.collection.smap;


import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.incDes.JALong;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with Long keys.
 */
public class LongSMap<VALUE_TYPE extends IncDesImpl> extends SMap<Long, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    final protected Factory getKeyFactory() {
        return Durables.getFactoryLocator(getMailbox()).getFactory(JALong.FACTORY_NAME);
    }

    /**
     * Converts a string to a key.
     *
     * @param skey The Long to be converted.
     * @return The key.
     */
    final protected Long stringToKey(String skey) {
        return new Long(skey);
    }
}
