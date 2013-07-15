package org.agilewiki.jactor2.util.durable.incDes;

import org.agilewiki.jactor2.api.BoundRequest;

/**
 * Fixed-length scalar
 *
 * @param <RESPONSE_TYPE> The type of scalar.
 */
public interface FLenS<RESPONSE_TYPE> extends IncDes {

    /**
     * Returns a boundRequest to get the value.
     *
     * @return The boundRequest.
     */
    BoundRequest<RESPONSE_TYPE> getValueReq();

    /**
     * Returns the value.
     *
     * @return The value.
     */
    RESPONSE_TYPE getValue() throws Exception;

    /**
     * Returns a boundRequest to assign a value.
     *
     * @param _value The new value.
     * @return The boundRequest.
     */
    BoundRequest<Void> setValueReq(final RESPONSE_TYPE _value);

    /**
     * Assigns a value.
     *
     * @param _value The new value.
     */
    void setValue(final RESPONSE_TYPE _value);
}
