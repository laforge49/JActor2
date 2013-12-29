package org.agilewiki.jactor2.modules.transactions.properties;

import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

import java.util.SortedMap;

/**
 * The content passed to subscribers of the validation and change RequestBus instances.
 */
public class ImmutablePropertyChanges {

    /**
     * The new version of the immutable properties map.
     */
    public final ImmutableProperties<Object> immutableProperties;

    /**
     * An unmodifiable sorted map of the property changes.
     */
    public final SortedMap<String, PropertyChange> readOnlyChanges;

    ImmutablePropertyChanges(final PropertiesChangeManager propertiesChangeManager) {
        immutableProperties = propertiesChangeManager.immutableProperties;
        readOnlyChanges = propertiesChangeManager.readOnlyChanges;
    }
}
