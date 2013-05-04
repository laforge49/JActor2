package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

/**
 * Bytes holds an array of bytes.
 */
public interface Bytes extends VLenS<byte[]> {

    /**
     * Factory name for a Bytes object.
     */
    public static final String FACTORY_NAME = "bytes";

    /**
     * Returns the object created by deserializing the contents.
     *
     * @return An object, or null.
     */
    Object getObject() throws Exception;

    /**
     * Assign the byte array from serializing an object.
     *
     * @param object The object to be serialized.
     */
    void setObject(Object object) throws Exception;
}
