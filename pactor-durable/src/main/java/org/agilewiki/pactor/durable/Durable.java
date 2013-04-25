package org.agilewiki.pactor.durable;

public interface Durable extends IncDes {
    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    int _size()
            throws Exception;

    /**
     * Returns the ith JID component.
     *
     * @param _i The index of the element of interest.
     * @return The ith JID component, or null if the index is out of range.
     */
    PASerializable _iGet(final int _i) throws Exception;

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param _i     The index of the desired element.
     * @param _bytes Holds the serialized data.
     * @throws Exception Any exceptions thrown while processing the request.
     */
    void _iSetBytes(final int _i, final byte[] _bytes)
            throws Exception;

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param _pathname A JID pathname.
     * @return A JID actor or null.
     * @throws Exception Any uncaught exception which occurred while processing the request.
     */
    public PASerializable _resolvePathname(final String _pathname)
            throws Exception;
}
