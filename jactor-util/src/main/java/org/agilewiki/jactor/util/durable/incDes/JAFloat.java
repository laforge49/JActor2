package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface JAFloat extends FLenS<Float> {

    /**
     * Size of a serialized JAFloat in bytes.
     */
    public final static int LENGTH = 4;

    /**
     * Factory name for a durable float.
     */
    public static final String FACTORY_NAME = "float";
}
