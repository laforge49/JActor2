package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import java.util.*;

/**
 * The change manager used by tssm transactions to update the tssMap.
 */
public class TSSMChangeManager<VALUE> implements AutoCloseable {

    private final TSSMap<VALUE> tssMap;

    private final SortedMap<String, VALUE> unmodifiableTSSMap;

    final protected List<TSSMChange<VALUE>> changes = new ArrayList<TSSMChange<VALUE>>();

    /**
     * An unmodifiable view of the changes.
     */
    final public List<TSSMChange<VALUE>> unmodifiableChanges = Collections.unmodifiableList(changes);

    private boolean closed;

    public TSSMChangeManager(final TSSMap<VALUE> _tssMap) {
        tssMap = _tssMap;
        unmodifiableTSSMap = Collections.unmodifiableSortedMap(tssMap);
    }

    /**
     * Returns the latest version of the isMap.
     *
     * @return The latest version of the isMap.
     */
    public SortedMap<String, VALUE> getUnmodifiableTSSMap() {
        return unmodifiableTSSMap;
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
        VALUE oldValue = tssMap.get(_key);
        if (oldValue == _newValue)
            return;
        if (oldValue != null && oldValue.equals(_newValue))
            return;
        if (_newValue == null)
            tssMap.remove(_key);
        else
            tssMap.put(_key, _newValue);
        TSSMChange<VALUE> tssmChange = new TSSMChange<VALUE>(_key, oldValue, _newValue);
        changes.add(tssmChange);
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
