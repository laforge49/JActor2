package org.agilewiki.jactor2.util.durable.incDes;

public interface JALong extends FLenS<Long> {

    /**
     * Size of a serialized JALong in bytes.
     */
    public static final int LENGTH = 8;

    /**
     * Factory name for JALong.
     */
    public static final String FACTORY_NAME = "long";
}
