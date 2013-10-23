package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.transactions.TransactionProcessor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class PropertiesProcessor extends TransactionProcessor
        <NavigableMap<String, Object>, PropertiesWrapper, PropertyChanges, SortedMap<String, Object>> {

    private SortedMap<String, Object> newImmutableState;
    private SortedMap<String, PropertyChange> immutableChanges;

    public PropertiesProcessor(IsolationReactor _isolationReactor) throws Exception {
        super(_isolationReactor, Collections.unmodifiableSortedMap(new TreeMap<String, Object>()));
    }

    @Override
    protected void newImmutableState() {
        immutableState = newImmutableState;
    }

    @Override
    protected PropertiesWrapper newStateWrapper() {
        NavigableMap<String, Object> newState = new TreeMap<String, Object>(immutableState);
        newImmutableState = Collections.unmodifiableSortedMap(newState);
        NavigableMap<String, PropertyChange> propertyChanges = new TreeMap<String, PropertyChange>();
        immutableChanges = Collections.unmodifiableSortedMap(propertyChanges);
        return new PropertiesWrapper(immutableState, newState, newImmutableState, propertyChanges, immutableChanges);
    }

    @Override
    protected PropertyChanges getChanges(PropertiesWrapper _stateWrapper) {
        return new PropertyChanges(immutableState, newImmutableState, immutableChanges);
    }
}
