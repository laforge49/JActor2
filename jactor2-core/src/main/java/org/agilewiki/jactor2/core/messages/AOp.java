package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * An asynchronous operation, optionally used to define an AsyncRequest.
 */
public abstract class AOp<RESPONSE_TYPE> implements
        AsyncOperation<RESPONSE_TYPE> {
    private static volatile int nextHash;
    private final String opName;
    public final ReactorBase targetReactor;
    /**
     * Our hashcode.
     */
    private final int hashCode = nextHash++;

    @Override
    public String getOpName() {
        return opName;
    }

    /**
     * Create an asynchronous operation.
     *
     * @param _opName        The name of the operation.
     * @param _targetReactor The reactor whose thread will process the operation.
     */
    public AOp(final String _opName, final Reactor _targetReactor) {
        opName = _opName;
        targetReactor = (ReactorBase) _targetReactor;
    }

    /**
     * Redefines the hashcode for a faster hashing.
     */
    @Override
    public int hashCode() {
        return hashCode;
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

    /**
     * The processAsyncOperation method will be invoked by the target Reactor on its own thread.
     *
     * @param _asyncRequestImpl       The request context--may be of a different RESPONSE_TYPE.
     * @param _asyncResponseProcessor Handles the response.
     */
    abstract protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception;

    @GwtIncompatible
    public RESPONSE_TYPE call() throws Exception {
        return PlantImpl.getSingleton()
                .createAsyncRequestImpl(this, targetReactor).call();
    }

    @Override
    public String toString() {
        return opName;
    }

    /**
     * Cancels all outstanding requests.
     * This method is thread safe, so it can be called from any thread.
     */
    public void cancelAll(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.cancelAll();
    }

    @Override
    public void onCancel(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onCancel(_asyncRequestImpl);
    }

    @Override
    public void onClose(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onClose(_asyncRequestImpl);
    }
}
