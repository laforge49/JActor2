package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface JALong extends FLenS<Long> {

    /**
     * Size of a serialized JALong in bytes.
     */
    public final static int LENGTH = 8;

    /**
     * Factory name for JALong.
     */
    public static final String FACTORY_NAME = "long";
}
