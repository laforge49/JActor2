package org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * A balanced tree that holds a map with String keys.
 */
public class StringBMap<VALUE_TYPE extends IncDesImpl> extends BMap<String, VALUE_TYPE> {
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
