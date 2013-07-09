package org.agilewiki.jactor2.util.durable.incDes;

public interface JAFloat extends FLenS<Float> {

    /**
     * Size of a serialized JAFloat in bytes.
     */
    public final static int LENGTH = 4;

    /**
     * Factory name for JAFloat.
     */
    public static final String FACTORY_NAME = "float";
}
