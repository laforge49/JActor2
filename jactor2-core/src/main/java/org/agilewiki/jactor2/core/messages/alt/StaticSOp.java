package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * A static synchronous operation, optionally used to define a SyncRequest.
 */
public abstract class StaticSOp<B extends Blade, RESPONSE_TYPE> extends
        StaticOpBase<B, RESPONSE_TYPE, RequestImplWithData<RESPONSE_TYPE>>
        implements SyncOperation<RESPONSE_TYPE> {
    /**
     * Create a static synchronous operation.
     *
     * @param bladeType The type of the owner Blade
     */
    public StaticSOp(final Class<B> bladeType) {
        super(bladeType);
    }

    /**
     * Creates a RequestImplWithData Request.
     *
     * @param targetReactor The target Reactor.
     * @return the RequestImplWithData Request.
     */
    @Override
    protected final RequestImplWithData<RESPONSE_TYPE> createInternalWithData(
            final Reactor targetReactor) {
        return PlantImpl.getSingleton().createSyncRequestImplWithData(this,
                targetReactor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final RESPONSE_TYPE doSync(
            @SuppressWarnings("rawtypes") final RequestImpl _requestImpl)
            throws Exception {
        final RequestImplWithData<RESPONSE_TYPE> req = (RequestImplWithData<RESPONSE_TYPE>) _requestImpl;
        return processSyncOperation(blade.get(req), req);
    }

    protected abstract RESPONSE_TYPE processSyncOperation(final B blade,
            RequestImplWithData<RESPONSE_TYPE> _requestImpl) throws Exception;
}
