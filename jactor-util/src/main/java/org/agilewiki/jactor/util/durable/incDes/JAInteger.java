package org.agilewiki.jactor.util.durable.incDes;

public interface JAInteger extends FLenS<Integer> {

    /**
     * Size of a serialized JAInteger in bytes.
     */
    public final static int LENGTH = 4;

    /**
     * Factory name for JAInteger.
     */
    public static final String FACTORY_NAME = "int";
}
