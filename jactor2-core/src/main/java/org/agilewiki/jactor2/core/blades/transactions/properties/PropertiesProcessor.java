package org.agilewiki.jactor2.core.blades.transactions.properties;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.agilewiki.jactor2.core.blades.transactions.Transaction;
import org.agilewiki.jactor2.core.blades.transactions.TransactionProcessor;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PropertiesProcessor
        extends
        TransactionProcessor<NavigableMap<String, Object>, PropertiesWrapper, PropertyChanges, SortedMap<String, Object>> {

    private SortedMap<String, Object> newImmutableState;
    private SortedMap<String, PropertyChange> immutableChanges;

    public PropertiesProcessor(final IsolationReactor _isolationReactor)
            throws Exception {
        super(_isolationReactor, Collections
                .unmodifiableSortedMap(new TreeMap<String, Object>()));
    }

    public PropertiesProcessor(final IsolationReactor _isolationReactor,
            final NonBlockingReactor _nonBlockingReactor) throws Exception {
        super(_isolationReactor, _nonBlockingReactor, Collections
                .unmodifiableSortedMap(new TreeMap<String, Object>()));
    }

    public PropertiesProcessor(final IsolationReactor _isolationReactor,
            final NonBlockingReactor _nonBlockingReactor,
            final SortedMap<String, Object> _initialState) throws Exception {
        super(_isolationReactor, _nonBlockingReactor, Collections
                .unmodifiableSortedMap(_initialState));
    }

    @Override
    protected void newImmutableState() {
        immutableState = newImmutableState;
    }

    @Override
    protected PropertiesWrapper newStateWrapper() {
        final NavigableMap<String, Object> newState = new TreeMap<String, Object>(
                immutableState);
        newImmutableState = Collections.unmodifiableSortedMap(newState);
        final NavigableMap<String, PropertyChange> propertyChanges = new TreeMap<String, PropertyChange>();
        immutableChanges = Collections.unmodifiableSortedMap(propertyChanges);
        return new PropertiesWrapper(immutableState, newState,
                newImmutableState, propertyChanges, immutableChanges);
    }

    @Override
    protected PropertyChanges newChanges() {
        return new PropertyChanges(immutableState, newImmutableState,
                immutableChanges);
    }

    public Transaction<PropertiesWrapper> putTransaction(final String _key,
            final Object _newValue) {
        return new Transaction<PropertiesWrapper>() {
            @Override
            public AsyncRequest<Void> updateAReq(
                    final PropertiesWrapper _stateWrapper) {
                return new AsyncBladeRequest<Void>() {
                    @Override
                    protected void processAsyncRequest() throws Exception {
                        _stateWrapper.put(_key, _newValue);
                        processAsyncResponse(null);
                    }
                };
            }
        };
    }

    public AsyncRequest<Void> putAReq(final String _key, final Object _newValue) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                final Transaction<PropertiesWrapper> putTran = putTransaction(
                        _key, _newValue);
                send(processTransactionAReq(putTran), this);
            }
        };
    }
}
