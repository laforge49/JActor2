package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.transactions.NewNotifierAReq;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.NavigableMap;
import java.util.SortedMap;

abstract public class NewPropertiesNotifierAReq extends NewNotifierAReq
        <NavigableMap<String, Object>, PropertiesWrapper, PropertyChanges, SortedMap<String, Object>> {
    public NewPropertiesNotifierAReq(
            final NonBlockingReactor _targetReactor,
            final PropertiesBlade _propertiesBlade) {
        super(_targetReactor, _propertiesBlade.getPropertiesProcessor());
    }
}
