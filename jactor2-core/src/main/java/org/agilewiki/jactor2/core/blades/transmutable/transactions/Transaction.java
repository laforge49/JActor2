package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A composable transaction.
 */
public interface Transaction<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        extends IsolationBlade {
    /**
     * Evaluate the transaction.
     *
     * @param _root      The root of the transaction chain, a transmutable reference.
     * @param _applyAReq The request to apply.
     * @param _dis       The response processor.
     */
    void _eval(final TransmutableReference<DATATYPE, TRANSMUTABLE> _root,
               final AsyncRequestImpl<TRANSMUTABLE> _applyAReq,
               final AsyncResponseProcessor<Void> _dis) throws Exception;
}
