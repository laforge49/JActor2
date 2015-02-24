package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncOperation;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * A static asynchronous operation, optionally used to define an AsyncRequest.
 */
public abstract class StaticAOp<B extends Blade, RESPONSE_TYPE> extends
        StaticOpBase<B, RESPONSE_TYPE, AsyncRequestImplWithData<RESPONSE_TYPE>>
        implements AsyncOperation<RESPONSE_TYPE> {

    /**
     * Create a static asynchronous operation.
     *
     * @param bladeType The type of the owner Blade
     */
    public StaticAOp(final Class<B> bladeType) {
        super(bladeType);
    }

    /**
     * Creates a RequestImplWithData Request.
     *
     * @param targetReactor The target Reactor.
     * @return the RequestImplWithData Request.
     */
    @Override
    protected final AsyncRequestImplWithData<RESPONSE_TYPE> createInternalWithData(
            final Reactor targetReactor) {
        return PlantImpl.getSingleton().createAsyncRequestImplWithData(this,
                targetReactor);
    }

    /**
     * Cancels all outstanding requests.
     * This method is thread safe, so it can be called from any thread.
     */
    public void cancelAll(
            @SuppressWarnings("rawtypes") final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.cancelAll();
    }

    @Override
    public void onCancel(
            @SuppressWarnings("rawtypes") final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onCancel(_asyncRequestImpl);
    }

    @Override
    public void onClose(
            @SuppressWarnings("rawtypes") final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onClose(_asyncRequestImpl);
    }

    @Override
    public void doAsync(final AsyncRequestImpl _asyncRequestImpl,
                        final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        if (!_asyncRequestImpl.getTargetReactor().asReactorImpl().isRunning())
            throw new IllegalStateException(
                    "Not thread safe: not called from within an active request");
        processAsyncOperation(_asyncRequestImpl, _asyncResponseProcessor);
    }

    @SuppressWarnings("unchecked")
    protected final void processAsyncOperation(
            @SuppressWarnings("rawtypes") final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        final AsyncRequestImplWithData<RESPONSE_TYPE> req = (AsyncRequestImplWithData<RESPONSE_TYPE>) _asyncRequestImpl;
        processAsyncOperation(blade.get(req), req, _asyncResponseProcessor);
    }

    protected abstract void processAsyncOperation(final B blade,
            final AsyncRequestImplWithData<RESPONSE_TYPE> _asyncRequestImpl,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception;
}
