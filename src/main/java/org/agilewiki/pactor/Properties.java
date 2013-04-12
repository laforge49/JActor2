package org.agilewiki.pactor;

/**
 * Manages a property set.
 */
public interface Properties {

    /**
     * Returns the named property, or null.
     *
     * @param _propertyName The name of the property.
     * @return The value, or null.
     */
    public Object getProperty(final String _propertyName)
            throws Exception;

    /**
     * Assign a value to a property.
     *
     * @param _propertyName  The name of the property.
     * @param _propertyValue The value to be assigned.
     */
    public void putProperty(final String _propertyName, final Object _propertyValue)
            throws Exception;
}
