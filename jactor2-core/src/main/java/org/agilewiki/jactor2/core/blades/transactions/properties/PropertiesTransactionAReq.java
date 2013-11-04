package org.agilewiki.jactor2.core.blades.transactions.properties;

import java.util.NavigableMap;
import java.util.SortedMap;

import org.agilewiki.jactor2.core.blades.transactions.TransactionAReq;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class PropertiesTransactionAReq
        extends
        TransactionAReq<NavigableMap<String, Object>, PropertiesWrapper, PropertyChanges, SortedMap<String, Object>> {
    public PropertiesTransactionAReq(final NonBlockingReactor _targetReactor,
            final PropertiesBlade _propertiesBlade) {
        super(_targetReactor, _propertiesBlade.getPropertiesProcessor());
    }
}
