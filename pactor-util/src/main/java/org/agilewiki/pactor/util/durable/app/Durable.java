package org.agilewiki.pactor.util.durable.app;

import org.agilewiki.pactor.util.durable.IncDes;
import org.agilewiki.pactor.util.durable.PASerializable;

public interface Durable extends IncDes {
    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    int _size();

    /**
     * Returns the ith JID component.
     *
     * @param _i The index of the element of interest.
     * @return The ith JID component, or null if the index is out of range.
     */
    PASerializable _iGet(final int _i);

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param _i     The index of the desired element.
     * @param _bytes Holds the serialized data.
     */
    void _iSetBytes(final int _i, final byte[] _bytes);

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param _pathname A JID pathname.
     * @return A JID actor or null.
     */
    public PASerializable _resolvePathname(final String _pathname);
}
