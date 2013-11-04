package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * A balanced tree that holds a map with Integer keys.
 */
public class IntegerBMap<VALUE_TYPE extends IncDesImpl> extends
        BMap<Integer, VALUE_TYPE> {
    /**
     * Converts a string to a key.
     *
     * @param skey The string to be converted.
     * @return The key.
     */
    @Override
    final protected Integer stringToKey(final String skey) {
        return new Integer(skey);
    }
}
