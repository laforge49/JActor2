package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.util.durable.JASerializable;

/**
 * A box optionally holds a serialized object of any type.
 */
public interface Box extends IncDes {

    /**
     * The factory name of a Box object.
     */
    public static final String FACTORY_NAME = "box";

    /**
     * Returns a request for getting the serializable object held by the box.
     *
     * @return The request.
     */
    Request<JASerializable> getValueReq();

    /**
     * Returns the serializable object held by the box.
     *
     * @return A serializable object, or null.
     */
    JASerializable getValue()
            throws Exception;

    /**
     * Returns a request for clearing the box.
     *
     * @return The request.
     */
    Request<Void> clearReq();

    /**
     * Clears the box.
     */
    void clear();

    /**
     * Returns a request for creating a new serializable object and put it in the box.
     *
     * @param _factoryName    The type of the new serializable object.
     * @return The request.
     */
    Request<Void> setValueReq(final String _factoryName);

    /**
     * Create a new serializable object and put it in the box.
     *
     * @param _factoryName    The type of the new serializable object.
     */
    void setValue(final String _factoryName)
            throws Exception;

    /**
     * Returns a request for creating and initializing a serializable object and put it in the box.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _factoryName    The type of the new serializable object.
     * @param _bytes          The content of the serializable object.
     * @return The request.
     */
    Request<Void> setValueReq(final String _factoryName, final byte[] _bytes);

    /**
     * Create and initialize a serialize object and put it in the box.
     * (The bytes are not copied and must not be subsequently modified.)
     *
     * @param _factoryName    The type of the new serializable object.
     * @param _bytes          The content of the new serializable object.
     */
    void setValue(final String _factoryName, final byte[] _bytes)
            throws Exception;

    /**
     * Returns a request to create a new serializable object and put it in the box if the box was empty.
     *
     * @param _factoryName    The type of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Request<Boolean> makeValueReq(final String _factoryName);

    /**
     * Create a new serializable object and put it in the box if the box was empty.
     *
     * @param _factoryName    The type of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Boolean makeValue(final String _factoryName)
            throws Exception;

    /**
     * Returns a request to create and initialize a serializable object and put it in the box if the box was empty.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _factoryName    The type of the new serializable object.
     * @param _bytes          The content of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Request<Boolean> makeValueReq(final String _factoryName, final byte[] _bytes);

    /**
     * Create and initialize a serializable object and put it in the box if the box was empty.
     * (The bytes are not copied and must not be subsequently modified.)
     *
     * @param _factoryName    The type of the new serializable object.
     * @param _bytes          The content of the new serializable object.
     * @return True if a new serializable object was created.
     */
    Boolean makeValue(final String _factoryName, final byte[] _bytes)
            throws Exception;
}
