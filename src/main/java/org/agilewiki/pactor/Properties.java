package org.agilewiki.pactor;

/**
 * Manages a property set.
 */
public interface Properties {

    /**
     * Returns the named property, or null.
     *
     * @param propertyName The name of the property.
     * @return The value, or null.
     */
    public Object getProperty(final String propertyName)
            throws Exception;

    /**
     * Assign a value to a property.
     *
     * @param propertyName  The name of the property.
     * @param propertyValue The value to be assigned.
     */
    public void setProperty(final String propertyName, final Object propertyValue)
            throws Exception;
}
