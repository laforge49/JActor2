package org.agilewiki.pactor;

import java.util.Iterator;
import java.util.Map;

/**
 * Manages a property set.
 */
public interface Properties extends Actor {

    /**
     * Returns the named property, or null.
     *
     * @param _propertyName The name of the property.
     * @return The value, or null.
     */
    Object getProperty(final String _propertyName)
            throws Exception;

    /**
     * Assign a value to a property.
     *
     * @param _propertyName  The name of the property.
     * @param _propertyValue The value to be assigned.
     */
    void putProperty(final String _propertyName, final Object _propertyValue)
            throws Exception;

    /**
     * Returns an iterator over the properties set in ascending order.
     *
     * @return An ascending iterator over the map entries.
     */
    Iterator<Map.Entry<String, Object>> iterator();
}
