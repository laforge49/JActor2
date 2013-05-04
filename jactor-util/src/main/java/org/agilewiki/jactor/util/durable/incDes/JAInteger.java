package org.agilewiki.jactor.util.durable.incDes;

import org.agilewiki.jactor.api.Request;

public interface JAInteger extends FLenS<Integer> {

    /**
     * Size of a serialized JAInteger in bytes.
     */
    public final static int LENGTH = 4;

    /**
     * Factory name for a durable int.
     */
    public static final String FACTORY_NAME = "int";
}
