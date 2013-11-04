package org.agilewiki.jactor2.util.durable.incDes;

public interface JAInteger extends FLenS<Integer> {

    /**
     * Size of a serialized JAInteger in bytes.
     */
    public static final int LENGTH = 4;

    /**
     * Factory name for JAInteger.
     */
    public static final String FACTORY_NAME = "int";
}
