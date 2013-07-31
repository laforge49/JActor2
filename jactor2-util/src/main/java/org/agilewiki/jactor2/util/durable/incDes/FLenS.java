package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.core.messaging.Request;

/**
 * Fixed-length scalar
 *
 * @param <RESPONSE_TYPE> The type of scalar.
 */
public interface FLenS<RESPONSE_TYPE> extends IncDes {

    /**
     * Returns a request to get the value.
     *
     * @return The request.
     */
    Request<RESPONSE_TYPE> getValueReq();

    /**
     * Returns the value.
     *
     * @return The value.
     */
    RESPONSE_TYPE getValue() throws Exception;

    /**
     * Returns a request to assign a value.
     *
     * @param _value The new value.
     * @return The request.
     */
    Request<Void> setValueReq(final RESPONSE_TYPE _value);

    /**
     * Assigns a value.
     *
     * @param _value The new value.
     */
    void setValue(final RESPONSE_TYPE _value);
}
