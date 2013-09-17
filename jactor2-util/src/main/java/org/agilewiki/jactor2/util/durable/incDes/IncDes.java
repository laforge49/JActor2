package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;

/**
 * IncDes is the interface for the durable part of all JASerializable objects.
 */
public interface IncDes extends JASerializable, Blade, Ancestor {

    /**
     * Factory name for a serializable object with no durable data.
     */
    public static final String FACTORY_NAME = "incdes";

    /**
     * Returns a request to get the serialized length.
     *
     * @return The request.
     */
    AsyncRequest<Integer> getSerializedLengthReq();

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The actual size of the byte array needed to serialize the persistent data.
     */
    int getSerializedLength()
            throws Exception;

    /**
     * Returns a request to get the serialized data.
     * (References to the serialized data are retained, so the byte array must not be updated.)
     *
     * @return The request.
     */
    AsyncRequest<byte[]> getSerializedBytesReq();

    /**
     * Returns the serialized data.
     * (References to the serialized data are retained, so the byte array must not be updated.)
     *
     * @return The serialized data.
     */
    byte[] getSerializedBytes()
            throws Exception;

    /**
     * Returns a request to copy the serialized data into a byte array.
     *
     * @param bytes  The destination byte array.
     * @param offset The starting offset into the array.
     * @return The request.
     */
    AsyncRequest<Integer> getSerializedBytesReq(byte[] bytes, int offset);

    /**
     * Copies the serialized data into a byte array.
     *
     * @param bytes  The destination byte array.
     * @param offset The starting offset into the array.
     * @return The revised offset.
     */
    int save(byte[] bytes, int offset)
            throws Exception;

    /**
     * Load the serialized data.
     *
     * @param _bytes The serialized data.
     */
    void load(final byte[] _bytes) throws Exception;

    /**
     * Load the serialized data.
     *
     * @param _bytes  A byte array that holds the serialized data.
     * @param _offset The starting offset of the serialized data.
     * @param _length The length of the serialized data to be loaded.
     * @return The updated offset.
     */
    int load(final byte[] _bytes, final int _offset, final int _length)
            throws Exception;

    /**
     * Returns a request to resolve a pathname.
     *
     * @param _pathname The pathname to the serializable object of interest.
     * @return The specified serializable object.
     */
    AsyncRequest<JASerializable> resolvePathnameReq(final String _pathname);

    /**
     * Resolves a pathname, returning a serializable object or null.
     *
     * @param _pathname A pathname.
     * @return A serializable object or null.
     */
    JASerializable resolvePathname(final String _pathname)
            throws Exception;

    /**
     * Returns the factory used to create the serializable object.
     *
     * @return The factory.
     */
    Factory getFactory();

    /**
     * Returns the name of the factory used to create the serializable object.
     *
     * @return The factory name, or null.
     */
    String getFactoryName();

    /**
     * Returns a request to copy the serializable object.
     *
     * @param _reactor The processing to be used by the new serializable object.
     * @return The request.
     */
    AsyncRequest<JASerializable> copyReq(final Reactor _reactor);

    /**
     * Copy the serializable object.
     *
     * @param _reactor The processing to be used by the new serializable object.
     * @return A deep copy of the serializable object with the same durable data.
     */
    JASerializable copy(final Reactor _reactor) throws Exception;

    /**
     * Check for equality.
     *
     * @param _serializable
     * @return True when of the same type and has the same durable content.
     */
    AsyncRequest<Boolean> isEqualReq(final JASerializable _serializable);
}
