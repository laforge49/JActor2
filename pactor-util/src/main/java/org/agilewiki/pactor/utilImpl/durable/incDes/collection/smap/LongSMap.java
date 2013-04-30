package org.agilewiki.pactor.utilImpl.durable.incDes.collection.smap;


import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.incDes.PALong;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesImpl;

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
        return Durables.getFactory(Durables.getFactoryLocator(getMailbox()), PALong.FACTORY_NAME);
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
