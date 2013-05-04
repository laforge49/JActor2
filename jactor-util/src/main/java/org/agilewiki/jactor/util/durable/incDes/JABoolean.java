package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

/**
 * A durable boolean.
 */
public interface JABoolean extends FLenS<Boolean> {

    /**
     * Size of a serialized JABoolean in bytes.
     */
    public final static int LENGTH = 1;

    /**
     * Factory name for a durable boolean.
     */
    public static final String FACTORY_NAME = "bool";
}
