package org.agilewiki.jactor2.modules.transactions.properties;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.util.immutable.HashTreePProperties;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.transactions.TransactionProcessor;

import java.util.Map;

/**
 * Transaction-based updates to an immutable properties map.
 */
public class PropertiesProcessor extends TransactionProcessor<PropertiesChangeManager, ImmutableProperties<Object>, ImmutablePropertyChanges> {

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

    public PropertiesProcessor(final NonBlockingReactor _parentReactor) throws Exception {
        super(_parentReactor, empty());
    }

    public PropertiesProcessor(final NonBlockingReactor _parentReactor,
                               Map<String, Object> _initialState) throws Exception {
        super(_parentReactor, from(_initialState));
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

    /**
     * A transactional put request.
     *
     * @param _key      The property name.
     * @param _newValue The new value.
     * @return The request.
     */
    public AsyncRequest<Void> putAReq(final String _key, final Object _newValue) {
        return new PropertiesTransactionAReq(parentReactor, this) {
            protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                _changeManager.put(_key, _newValue);
            }
        };
    }

    /**
     * A transactional compare and set request.
     * A put is performed only if the old value was equal to the expected value.
     *
     * @param _key           The property name.
     * @param _expectedValue The new value.
     * @param _newValue      The new value.
     * @return The request.
     */
    public AsyncRequest<Void> compareAndSetAReq(final String _key, final Object _expectedValue, final Object _newValue) {
        return new PropertiesTransactionAReq(parentReactor, this) {
            protected void update(final PropertiesChangeManager _changeManager) throws Exception {
                Object oldValue = _changeManager.getImmutableProperties().get("stdout");
                if ((oldValue != null && oldValue.equals(_expectedValue) ||
                        (oldValue == null && _expectedValue == null))) {
                    _changeManager.put(_key, _newValue);
                }
            }
        };
    }

    public String toString() {
        return getImmutableState().toString();
    }
}
