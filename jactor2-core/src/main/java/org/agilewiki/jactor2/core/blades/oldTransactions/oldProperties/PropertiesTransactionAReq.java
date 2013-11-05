package org.agilewiki.jactor2.core.blades.oldTransactions.oldProperties;

import org.agilewiki.jactor2.core.blades.oldTransactions.TransactionAReq;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.NavigableMap;
import java.util.SortedMap;

abstract public class PropertiesTransactionAReq
        extends
        TransactionAReq<NavigableMap<String, Object>, PropertiesWrapper, PropertyChanges, SortedMap<String, Object>> {
    public PropertiesTransactionAReq(final NonBlockingReactor _targetReactor,
            final PropertiesBlade _propertiesBlade) {
        super(_targetReactor, _propertiesBlade.getPropertiesProcessor());
    }
}
