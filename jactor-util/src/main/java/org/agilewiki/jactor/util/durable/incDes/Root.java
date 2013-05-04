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
     * Returns the descriptor of the bundle whose factoryLocator can instantiate the objects
     * in this tree.
     *
     * @return A descriptor in the form bundleName|version|location.
     */
    String getDescriptor()
            throws Exception;
}
