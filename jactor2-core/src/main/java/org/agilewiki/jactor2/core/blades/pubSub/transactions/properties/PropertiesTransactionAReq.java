package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.transactions.TransactionAReq;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.core.reactors.CommonReactor;

public class PropertiesTransactionAReq
        extends TransactionAReq<PropertiesChangeManager, ImmutableProperties<Object>, ImmutablePropertyChanges> {
    public PropertiesTransactionAReq(final CommonReactor _updateReactor,
                                     final PropertiesProcessor _transactionProcessor) {
        super(_updateReactor, _transactionProcessor);
    }
}
