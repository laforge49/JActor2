package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

/**
 * A durable boolean.
 */
public interface JABoolean extends IncDes {

    /**
     * Size of a serialized JABoolean in bytes.
     */
    public final static int LENGTH = 1;

    /**
     * Factory name for a durable boolean.
     */
    public static final String FACTORY_NAME = "bool";

    /**
     * Returns a request to get the value.
     *
     * @return The request.
     */
    Request<Boolean> getValueReq();

    /**
     * Returns the value.
     *
     * @return The value.
     */
    Boolean getValue();

    /**
     * Returns a request to assign a value.
     *
     * @param _value The new value.
     * @return The request.
     */
    Request<Void> setValueReq(final Boolean _value);

    /**
     * Assigns a value.
     *
     * @param _value The new value.
     */
    void setValue(final Boolean _value);
}
