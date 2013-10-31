package org.agilewiki.jactor2.util;

/**
 * Implements immutable dependency stack injection.
 */
public class AncestorBase implements Ancestor {

    /**
     * True when initialized, this flag prevents duplicate initialization.
     */
    private boolean initialized;

    /**
     * Returns an ancestor, excluding the child, which is an instance of the target class.
     *
     * @param child       The blades who's dependency stack is searched.
     * @param targetClass The class used to select the ancestor.
     * @return The ancestor that is an instance of the target class, or null.
     */
    public static Ancestor getAncestor(final Ancestor child,
                                       final Class targetClass) {
        if (child == null)
            return null;
        return getMatch(child.getParent(), targetClass);
    }

    /**
     * Returns the child, or an ancestor, which is an instance of the target class.
     *
     * @param child       The blades who's dependency stack is searched.
     * @param targetClass The class used to select the child or one of its ancestors.
     * @return An blades that implements the target class, or null.
     */
    public static Ancestor getMatch(Ancestor child, final Class targetClass) {
        while (child != null) {
            if (targetClass.isInstance(child))
                return child;
            child = child.getParent();
        }
        return null;
    }

    /**
     * The top of the immutable dependency stack.
     */
    private Ancestor parent;

    /**
     * Returns true when the blades has been initialized.
     *
     * @return True when the blades has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize with no ancestor stack.
     */
    final public void initialize() throws Exception {
        initialize(null);
    }

    /**
     * Initialize with an ancestor stack.
     *
     * @param _parent The top of the immutable dependency stack.
     */
    public void initialize(final Ancestor _parent) {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        initialized = true;
        parent = _parent;
    }

    @Override
    public Ancestor getParent() {
        return parent;
    }
}
