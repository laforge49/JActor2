package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The change manager used by ism transactions to update the immutable string map.
 */
public class ImmutableChangeManager<VALUE> implements AutoCloseable {

    private ISMap<VALUE> isMap;

    final TreeMap<String, ImmutableChange<VALUE>> changes = new TreeMap<String, ImmutableChange<VALUE>>();

    /**
     * An unmodifiable view of the immutable changes.
     */
    final public SortedMap<String, ImmutableChange<VALUE>> readOnlyChanges = Collections.unmodifiableSortedMap(changes);

    private boolean closed;

    public ImmutableChangeManager(final ISMap<VALUE> _isMap) {
        isMap = _isMap;
    }

    /**
     * Returns the latest version of the isMap.
     *
     * @return The latest version of the isMap.
     */
    public ISMap<VALUE> getISMap() {
        return isMap;
    }

    /**
     * Update the isMap.
     *
     * @param _key      The name.
     * @param _newValue The new value, or null.
     */
    public void put(final String _key, final VALUE _newValue) {
        if (closed) {
            throw new IllegalStateException(
                    "Already closed, the transaction is complete.");
        }
        if (_key == null)
            throw new IllegalArgumentException("key may not be null");
        VALUE oldValue = isMap.get(_key);
        if (oldValue == _newValue)
            return;
        if (oldValue != null && oldValue.equals(_newValue))
            return;
        if (_newValue == null)
            isMap = isMap.minus(_key);
        else
            isMap = isMap.plus(_key, _newValue);
        ImmutableChange<VALUE> immutableChange = new ImmutableChange<VALUE>(_key, oldValue, _newValue);
        changes.put(_key, immutableChange);
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
