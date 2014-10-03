package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.transmutable.transactions.Transaction;

import java.util.SortedMap;

/**
 * A TSSMap transaction.
 */
abstract public class TSSMTransaction<VALUE> extends Transaction<SortedMap<String, VALUE>, TSSMap<VALUE>> {

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one, or null.
     */
    TSSMTransaction(final TSSMTransaction<VALUE> _parent) {
        super(_parent);
    }
}
