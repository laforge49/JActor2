package org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * A balanced tree that holds a map with Long keys.
 */
public class LongBMap<VALUE_TYPE extends IncDesImpl> extends BMap<Long, VALUE_TYPE> {
    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    final protected Long stringToKey(String skey) {
        return new Long(skey);
    }
}
