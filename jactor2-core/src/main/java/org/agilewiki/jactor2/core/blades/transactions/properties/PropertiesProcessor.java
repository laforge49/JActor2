package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.transactions.TransactionProcessor;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.util.immutable.HashTreePProperties;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

import java.util.Map;

/**
 * Transaction-based updates to an immutable properties map.
 */
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

    /**
     * Create a PropertiesProcessor.
     *
     * @param _isolationReactor The isolation reactor used to isolate the transactions.
     */
    public PropertiesProcessor(IsolationReactor _isolationReactor) throws Exception {
        super(_isolationReactor, empty());
    }

    /**
     * Create a PropertiesProcessor.
     *
     * @param _isolationReactor The isolation reactor used to isolate the transactions.
     * @param _initialState     The initial state of the property map.
     */
    public PropertiesProcessor(IsolationReactor _isolationReactor,
                               Map<String, Object> _initialState) throws Exception {
        super(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), from(_initialState));
    }

    /**
     * Create a PropertiesProcessor.
     *
     * @param _isolationReactor The isolation reactor used to isolate the transactions.
     * @param _commonReactor    The reactor used for transaction processing and by the two ReactorBus instances.
     * @param _initialState     The initial state of the property map.
     */
    public PropertiesProcessor(final IsolationReactor _isolationReactor,
                               final NonBlockingReactor _commonReactor,
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

    /**
     * A transactional put request.
     *
     * @param _key      The property name.
     * @param _newValue The new value.
     * @return The request.
     */
    public AsyncRequest<Void> putAReq(final String _key, final Object _newValue) {
        return new PropertiesTransactionAReq(commonReactor, this) {
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
        return new PropertiesTransactionAReq(commonReactor, this) {
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
