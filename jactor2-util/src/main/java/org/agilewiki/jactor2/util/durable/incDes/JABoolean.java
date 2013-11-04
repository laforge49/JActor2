package org.agilewiki.jactor2.util.durable.incDes;

/**
 * A durable boolean.
 */
public interface JABoolean extends FLenS<Boolean> {

    /**
     * Size of a serialized JABoolean in bytes.
     */
    public static final int LENGTH = 1;

    /**
     * Factory name for JABoolean.
     */
    public static final String FACTORY_NAME = "bool";
}
