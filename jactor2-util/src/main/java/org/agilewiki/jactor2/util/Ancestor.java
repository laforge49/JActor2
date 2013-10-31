package org.agilewiki.jactor2.util;

/**
 * The Ancestor interface supports injection of an immutable dependency stack.
 */
public interface Ancestor {
    /**
     * Returns the parent blades in the dependency stack.
     *
     * @return The parent blades, or null.
     */
    public Ancestor getParent();
}
