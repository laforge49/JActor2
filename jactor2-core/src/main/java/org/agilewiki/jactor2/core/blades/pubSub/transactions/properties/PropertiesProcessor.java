package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.transactions.TransactionProcessor;
import org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable.ImmutableProperties;
import org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable.HashTreePProperties;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Map;

public class PropertiesProcessor extends TransactionProcessor
        <PropertiesChangeManager, ImmutableProperties<Object>, ImmutablePropertyChanges> {

    static <V> ImmutableProperties<V> empty() {
        return HashTreePProperties.empty();
    }

    static <V> ImmutableProperties<V> singleton(String key, V value) {
        return HashTreePProperties.singleton(key, value);
    }

    static <V> ImmutableProperties<V> from(Map<String, V> m) {
        return HashTreePProperties.from(m);
    }

    PropertiesChangeManager propertiesChangeManager;

    public PropertiesProcessor(IsolationReactor _isolationReactor) throws Exception {
        super(_isolationReactor, empty());
    }

    public PropertiesProcessor(IsolationReactor _isolationReactor,
                               Map<String, Object> _initialState) throws Exception {
        super(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), from(_initialState));
    }

    public PropertiesProcessor(final IsolationReactor _isolationReactor,
                               final CommonReactor _commonReactor,
                               final Map<String, Object> _initialState) throws Exception {
        super(_isolationReactor, _commonReactor, from(_initialState));
    }

    @Override
    protected PropertiesChangeManager newChangeManager() {
        propertiesChangeManager = new PropertiesChangeManager(immutableState);
        return propertiesChangeManager;
    }

    @Override
    protected ImmutablePropertyChanges newChanges() {
        return new ImmutablePropertyChanges(propertiesChangeManager);
    }

    @Override
    protected void newImmutableState() {
        immutableState = propertiesChangeManager.immutableProperties;
    }

    public AsyncRequest<Void> putAReq(final String _key, final Object _newValue) {
        return new PropertiesTransactionAReq(commonReactor, this) {
            protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                _changeManager.put(_key, _newValue);
            }
        };
    }
}
