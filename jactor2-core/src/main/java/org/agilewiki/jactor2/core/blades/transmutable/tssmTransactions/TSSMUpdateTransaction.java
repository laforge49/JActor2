package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

/**
 * Composable transaction to update a property.
 */
public class TSSMUpdateTransaction<VALUE> extends TSSMTransaction<VALUE> {

    public final String name;

    public final VALUE value;

    public final boolean expecting;

    public final VALUE expectedValue;

    /**
     * Create a Transaction.
     *
     * @param _name  The name.
     * @param _value The value.
     */
    public TSSMUpdateTransaction(final String _name,
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
    public TSSMUpdateTransaction(final String _name,
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
    public TSSMUpdateTransaction(final String _name,
                                final VALUE _value,
                                final TSSMTransaction<VALUE> _parent) {
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
    public TSSMUpdateTransaction(final String _name,
                                final VALUE _value,
                                final VALUE _expectedValue,
                                final TSSMTransaction<VALUE> _parent) {
        super(_parent);
        name = _name;
        value = _value;
        expecting = true;
        expectedValue = _expectedValue;
    }

    @Override
    protected void update(TSSMap<VALUE> _transmutable) throws Exception {
        if (value == null)
            _transmutable.remove(name);
        else
            _transmutable.put(name, value);
        tssmChangeManager.put(name, value);
    }

    @Override
    protected boolean precheck(final TSSMap<VALUE> tssMap) {
        VALUE old = tssMap.get(name);
        return !expecting || (expectedValue == null && old == null) ||
                (expectedValue != null && expectedValue.equals(old));
    }

    @Override
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: " + name + " = " + value);
    }
}
