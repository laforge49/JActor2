package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;

/**
 * Variable-length scalar
 *
 * @param <RESPONSE_TYPE> The type of scalar.
 */
public interface VLenS<RESPONSE_TYPE> extends FLenS<RESPONSE_TYPE> {

    /**
     * Returns a request to set the contents to null.
     *
     * @return The request.
     */
    AsyncRequest<Void> clearReq();

    /**
     * Sets the contents to null.
     */
    void clear();

    /**
     * Returns a request to assign new value if the old value was null.
     *
     * @param _value The new value.
     * @return The request.
     */
    AsyncRequest<Boolean> makeValueReq(final RESPONSE_TYPE _value);

    /**
     * Assign a new value if the old value was null.
     *
     * @param _value The new value.
     * @return True if the old value was null.
     */
    Boolean makeValue(final RESPONSE_TYPE _value);
}
