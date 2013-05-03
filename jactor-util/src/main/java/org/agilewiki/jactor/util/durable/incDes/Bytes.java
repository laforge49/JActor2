package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

/**
 * Bytes holds an array of bytes.
 */
public interface Bytes extends IncDes {

    /**
     * Factory name for a Bytes object.
     */
    public static final String FACTORY_NAME = "bytes";

    /**
     * Returns a request to get the content.
     *
     * @return The request.
     */
    Request<byte[]> getValueReq();

    /**
     * Returns the content.
     *
     * @return A copy of the content, or null.
     */
    byte[] getValue()
            throws Exception;

    /**
     * Returns the object created by deserializing the contents.
     *
     * @return An object, or null.
     */
    Object getObject() throws Exception;

    /**
     * Returns a request to set the contents to null.
     *
     * @return The request.
     */
    Request<Void> clearReq();

    /**
     * Sets the contents to null.
     */
    void clear();

    /**
     * Returns a request to assign new content.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _bytes The new content.
     * @return The request
     */
    Request<Void> setValueReq(final byte[] _bytes);

    /**
     * Assigns new content.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _bytes The new content.
     */
    void setValue(final byte[] _bytes);

    /**
     * Assign the byte array from serializing an object.
     *
     * @param object The object to be serialized.
     */
    void setObject(Object object) throws Exception;

    /**
     * Returns a request to assign new content if the content was null.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _bytes The new content.
     * @return The request.
     */
    Request<Boolean> makeValueReq(final byte[] _bytes);

    /**
     * Assign new content if the content was null.
     * (The byte array is not copied and should not be subsequently modified.)
     *
     * @param _bytes The new content.
     * @return True if the content was null.
     */
    Boolean makeValue(final byte[] _bytes);
}
