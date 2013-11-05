package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import com.google.common.collect.ImmutableSortedMap;
import org.agilewiki.jactor2.core.blades.pubSub.transactions.TransactionProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PropertiesProcessor extends TransactionProcessor
        <PropertiesChangeManager, SortedMap<String, Object>, ImmutablePropertyChanges> {

    static SortedMap<String, Object> makeImmutableState(Map<String, Object> mutableState) {
        ImmutableSortedMap.Builder<String, Object> stateBuilder = ImmutableSortedMap.naturalOrder();
        stateBuilder.putAll(mutableState);
        return stateBuilder.build();
    }

    ImmutableSortedMap.Builder<String, Object> stateBuilder;

    ImmutableSortedMap.Builder<String, PropertyChange> changeBuilder;

    SortedMap<String, Object> newImmutableProperties;

    public PropertiesProcessor(IsolationReactor _isolationReactor) throws Exception {
        this(_isolationReactor, makeImmutableState(new TreeMap()));
    }

    public PropertiesProcessor(IsolationReactor _isolationReactor,
                               Map<String, Object> _initialState) throws Exception {
        this(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), makeImmutableState(_initialState));
    }

    public PropertiesProcessor(final IsolationReactor _isolationReactor,
                                final CommonReactor _commonReactor,
                                final Map<String, Object> _initialState) throws Exception {
        super(_isolationReactor, _commonReactor, makeImmutableState(_initialState));
        stateBuilder = ImmutableSortedMap.naturalOrder();
        stateBuilder.putAll(_initialState);
    }

    @Override
    protected PropertiesChangeManager newChangeManager() {
        changeBuilder = ImmutableSortedMap.naturalOrder();
        return new PropertiesChangeManager(immutableState, stateBuilder, changeBuilder);
    }

    @Override
    protected ImmutablePropertyChanges newChanges() {
        newImmutableProperties = stateBuilder.build();
        return new ImmutablePropertyChanges(immutableState, newImmutableProperties, changeBuilder.build());
    }

    @Override
    protected void newImmutableState() {
        immutableState = newImmutableProperties;
    }
}
