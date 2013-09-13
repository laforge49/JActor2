package org.agilewiki.jactor2.util;

/**
 * The Ancestor interface supports injection of an immutable dependency stack.
 */
public interface Ancestor {
    /**
     * Returns the parent blade in the dependency stack.
     *
     * @return The parent blade, or null.
     */
    public Ancestor getParent();
}
