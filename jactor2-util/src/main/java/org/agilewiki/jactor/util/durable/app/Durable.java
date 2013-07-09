package org.agilewiki.jactor.util.durable.app;

import org.agilewiki.jactor.util.durable.JASerializable;
import org.agilewiki.jactor.util.durable.incDes.IncDes;

public interface Durable extends IncDes {

    /**
     * Returns the size of the tuple.
     *
     * @return The size of the tuple.
     */
    int _size();

    /**
     * Returns the ith element of the tuple.
     *
     * @param _i The index.
     * @return The ith element, or null if the index is out of range.
     */
    JASerializable _iGet(final int _i)
            throws Exception;

    /**
     * Initialize an element.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _i     The index.
     * @param _bytes The serialized data used to initialize the element.
     */
    void _iSetBytes(final int _i, final byte[] _bytes)
            throws Exception;

    /**
     * Resolves a pathname, returning a serializable object or null.
     *
     * @param _pathname A pathname.
     * @return A serializable object or null.
     */
    public JASerializable _resolvePathname(final String _pathname)
            throws Exception;
}
