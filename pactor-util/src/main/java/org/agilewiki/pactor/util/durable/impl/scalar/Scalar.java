package org.agilewiki.pactor.util.durable.impl.scalar;

import org.agilewiki.pactor.util.durable.impl.IncDesImpl;

/**
 * A IncDesImpl actor that hold a single value.
 */
abstract public class Scalar<SET_TYPE, RESPONSE_TYPE>
        extends IncDesImpl {

    /**
     * Assign a value.
     *
     * @param request The MakeValue request.
     * @throws Exception Any uncaught exception raised.
     */
    abstract public void setValue(SET_TYPE request)
            throws Exception;

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     * @throws Exception Any uncaught exception raised during deserialization.
     */
    abstract public RESPONSE_TYPE getValue()
            throws Exception;
}
