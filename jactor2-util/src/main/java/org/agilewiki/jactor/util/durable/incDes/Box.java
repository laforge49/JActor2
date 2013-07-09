package org.agilewiki.jactor.util.durable.incDes;

/**
 * A box optionally holds a serialized object of any type.
 */
public interface Box extends Union {

    /**
     * The factory name of a Box object.
     */
    public static final String FACTORY_NAME = "box";
}
