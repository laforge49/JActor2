package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * A balanced tree that holds a map with String keys.
 */
public class StringBMap<VALUE_TYPE extends IncDesImpl> extends
        BMap<String, VALUE_TYPE> {
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
