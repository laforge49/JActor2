package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.durable.JASerializable;

/**
 * A union can hold any one of a set of types of serializable objects.
 */
public interface Union extends IncDes {

    /**
     * Returns a request for getting the serializable object held by the union.
     *
     * @return The request.
     */
    AsyncRequest<JASerializable> getValueReq();

    /**
     * Returns the serializable object held by the union.
     *
     * @return A serializable object, or null.
     */
    JASerializable getValue() throws Exception;

    /**
     * Returns a request for clearing the union.
     *
     * @return The request.
     */
    AsyncRequest<Void> clearReq();

    /**
     * Clears the union.
     */
    void clear() throws Exception;

    /**
     * Returns a request for creating a new serializable object and put it in the union.
     *
     * @param _factoryName The type of the new serializable object.
     * @return The request.
     */
    AsyncRequest<Void> setValueReq(final String _factoryName);

    /**
     * Create a new serializable object and put it in the union.
     *
     * @param _factoryName The type of the new serializable object.
     */
    void setValue(final String _factoryName) throws Exception;

    /**
     * Returns a request for creating and initializing a serializable object and put it in the union.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _factoryName The type of the new serializable object.
     * @param _bytes       The content of the serializable object.
     * @return The request.
     */
    AsyncRequest<Void> setValueReq(final String _factoryName,
            final byte[] _bytes);

    /**
     * Create and initialize a serialize object and put it in the union.
     * (The bytes are not copied and must not be subsequently modified.)
     *
     * @param _factoryName The type of the new serializable object.
     * @param _bytes       The content of the new serializable object.
     */
    void setValue(final String _factoryName, final byte[] _bytes)
            throws Exception;

    /**
     * Returns a request to create a new serializable object and put it in the union if the union was empty.
     *
     * @param _factoryName The type of the new serializable object.
     * @return True if a new serializable object was created.
     */
    AsyncRequest<Boolean> makeValueReq(final String _factoryName);

    /**
     * Create a new serializable object and put it in the union if the union was empty.
     *
     * @param _factoryName The type of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Boolean makeValue(final String _factoryName) throws Exception;

    /**
     * Returns a request to create and initialize a serializable object and put it in the union if the union was empty.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _factoryName The type of the new serializable object.
     * @param _bytes       The content of the new serializable object.
     * @return True if a new serializable object was created.
     */
    AsyncRequest<Boolean> makeValueReq(final String _factoryName,
            final byte[] _bytes);

    /**
     * Create and initialize a serializable object and put it in the union if the union was empty.
     * (The bytes are not copied and must not be subsequently modified.)
     *
     * @param _factoryName The type of the new serializable object.
     * @param _bytes       The content of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Boolean makeValue(final String _factoryName, final byte[] _bytes)
            throws Exception;
}
