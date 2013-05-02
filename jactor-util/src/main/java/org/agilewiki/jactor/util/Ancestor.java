package org.agilewiki.jactor.util;

/**
 * The Ancestor interface supports injection of an immutable dependency stack.
 */
public interface Ancestor {
    /**
     * Returns the parent actor in the dependency stack.
     *
     * @return The parent actor, or null.
     */
    public Ancestor getParent();
}
