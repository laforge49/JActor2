package org.agilewiki.pactor;

import java.util.Map;

/**
 * Manages a (possibly hierarchical) property set.
 */
public interface Properties {

    /**
     * Returns the named property, or null.
     *
     * @param _propertyName The name of the property.
     * @return The value, or null.
     */
    Object getProperty(final String _propertyName);

    /**
     * Assign a value to a property.
     *
     * @param _propertyName  The name of the property.
     * @param _propertyValue The value to be assigned.
     */
    void putProperty(final String _propertyName, final Object _propertyValue);

    /**
     * Copies all the properties into the specified map.
     *
     * @param _map The map to be updated
     */
    void copyTo(final Map<String, Object> _map);
}
