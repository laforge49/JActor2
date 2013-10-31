package org.agilewiki.jactor2.util;

/**
 * Implements Named with an effectively final value.
 */
public class NamedBase implements Named {
    /**
     * The name, or null.
     */
    private String name;

    /**
     * Returns the immutable name.
     *
     * @return The name, or null.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Assigns a name, unless already assigned.
     *
     * @param _name The blades name.
     */
    public void setName(final String _name) {
        if (name != null)
            throw new UnsupportedOperationException("Already named: " + name);
        name = _name;
    }
}
