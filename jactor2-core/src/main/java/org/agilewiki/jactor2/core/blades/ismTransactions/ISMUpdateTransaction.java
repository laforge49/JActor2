package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.transactions.ImmutableSource;

/**
 * Composable transaction to update a property.
 */
public class ISMUpdateTransaction<VALUE> extends ISMSyncTransaction<VALUE> {

    private final String name;

    private final VALUE value;

    private final boolean expecting;

    private final VALUE expectedValue;

    /**
     * Create a Transaction.
     *
     * @param _name  The name.
     * @param _value The value.
     */
    public ISMUpdateTransaction(final String _name,
                                final VALUE _value) {
        name = _name;
        value = _value;
        expecting = false;
        expectedValue = null;
    }

    /**
     * Create a Transaction.
     *
     * @param _name          The name.
     * @param _value         The value.
     * @param _expectedValue The expected value.
     */
    public ISMUpdateTransaction(final String _name,
                                final VALUE _value,
                                final VALUE _expectedValue) {
        name = _name;
        value = _value;
        expecting = true;
        expectedValue = _expectedValue;
    }

    /**
     * Compose a Transaction to update a property.
     *
     * @param _name   The name.
     * @param _value  The value.
     * @param _parent The isMap transaction to be applied before this one.
     */
    public ISMUpdateTransaction(final String _name,
                                final VALUE _value,
                                final ISMTransaction<VALUE> _parent) {
        super(_parent);
        name = _name;
        value = _value;
        expecting = false;
        expectedValue = null;
    }

    /**
     * Compose a Transaction.
     *
     * @param _name          The name.
     * @param _value         The value.
     * @param _expectedValue The expected value.
     * @param _parent        The isMap transaction to be applied before this one.
     */
    public ISMUpdateTransaction(final String _name,
                                final VALUE _value,
                                final VALUE _expectedValue,
                                final ISMTransaction<VALUE> _parent) {
        super(_parent);
        name = _name;
        value = _value;
        expecting = true;
        expectedValue = _expectedValue;
    }

    /**
     * Updates the immutable data structure.
     *
     * @param source The Transaction or ImmutableReference holding the immutable to be operated on.
     */
    @Override
    protected void update(ImmutableSource<ISMap<VALUE>> source) throws Exception {
        if (value == null)
            immutable = source.getImmutable().minus(name);
        else
            immutable = source.getImmutable().plus(name, value);
        immutableChangeManager.put(name, value);
    }

    @Override
    protected boolean precheck(final ISMap<VALUE> isMap) {
        VALUE old = isMap.get(name);
        return !expecting || (expectedValue == null && old == null) ||
                (expectedValue != null && expectedValue.equals(old));
    }

    @Override
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: " + name + " = " + value);
    }
}
