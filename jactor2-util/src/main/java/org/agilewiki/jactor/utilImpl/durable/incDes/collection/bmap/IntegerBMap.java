package org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * A balanced tree that holds a map with Integer keys.
 */
public class IntegerBMap<VALUE_TYPE extends IncDesImpl> extends BMap<Integer, VALUE_TYPE> {
    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    final protected Integer stringToKey(String skey) {
        return new Integer(skey);
    }
}
