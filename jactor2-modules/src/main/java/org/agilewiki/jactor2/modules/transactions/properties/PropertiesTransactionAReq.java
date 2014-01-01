package org.agilewiki.jactor2.modules.transactions.properties;

import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.transactions.TransactionAReq;

/**
 * <p>
 * An AsyncRequest for processing immutable property transactions.
 * </p>
 * <p>
 * One of the update methods must be overridden or the transaction will throw an UnimplementedOperationException.
 * </p>
 */
public class PropertiesTransactionAReq
        extends TransactionAReq<PropertiesChangeManager, ImmutableProperties<Object>, ImmutablePropertyChanges> {

    /**
     * Create a transaction request.
     *
     * @param _updateReactor       The reactor to be used when updating the properties change manager.
     * @param _propertiesProcessor The properties processor to be used.
     */
    public PropertiesTransactionAReq(final CommonReactor _updateReactor,
                                     final PropertiesProcessor _propertiesProcessor) {
        super(_updateReactor, _propertiesProcessor);
    }
}
