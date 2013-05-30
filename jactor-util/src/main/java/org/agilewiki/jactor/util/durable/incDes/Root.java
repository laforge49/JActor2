package org.agilewiki.jactor.util.durable.incDes;

/**
 * The root node in a tree of serializable objects.
 */
public interface Root extends Box {

    /**
     * The factory name of a Root object.
     */
    public static final String FACTORY_NAME = "root";

    /**
     * Returns the location of the bundle needed to deserialize the Root contents.
     *
     * @return The associated bundle location.
     */
    String getBundleLocation()
            throws Exception;
}
