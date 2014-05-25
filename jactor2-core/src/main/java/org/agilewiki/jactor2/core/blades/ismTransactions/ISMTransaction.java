package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.blades.transactions.Transaction;

/**
 * An ISMap transaction.
 */
public interface ISMTransaction<VALUE> extends Transaction<ISMap<VALUE>> {
    ImmutableChangeManager<VALUE> getImmutableChangeManager();
}
