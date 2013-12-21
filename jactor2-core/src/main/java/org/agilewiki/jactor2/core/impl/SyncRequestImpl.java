package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.Request;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.plant.PoolThread;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class SyncRequestImpl<RESPONSE_TYPE> extends
        RequestImplBase<RESPONSE_TYPE> {

    private final SyncRequest<RESPONSE_TYPE> syncRequest;

    public SyncRequestImpl(final SyncRequest _syncRequest, final Reactor _targetReactor) {
        super(_targetReactor);
        syncRequest = _syncRequest;
    }

    public Request asRequest() {
        return syncRequest;
    }

    @Override
    protected void processRequestMessage() throws Exception {
        processObjectResponse(syncRequest.processSyncRequest());
    }

    /**
     * Process the request immediately.
     *
     * @param _source The targetReactor on whose thread this method was invoked and which
     *                must be the same as the targetReactor of the target.
     * @return The value returned by the target blades.
     */
    public RESPONSE_TYPE doLocal(final ReactorImpl _source) throws Exception {
        use();
        final ReactorImpl messageProcessor = ((Reactor) _source).asReactorImpl();
        if (PlantImpl.DEBUG) {
            if (messageProcessor instanceof ThreadBoundReactorImpl) {
                if (Thread.currentThread() instanceof PoolThread) {
                    throw new IllegalStateException("send from wrong thread");
                }
            } else {
                if (messageProcessor.getThreadReference().get() != Thread
                        .currentThread()) {
                    throw new IllegalStateException("send from wrong thread");
                }
            }
        }
        if (!messageProcessor.isRunning()) {
            throw new IllegalStateException(
                    "A valid source targetReactor can not be idle");
        }
        if (messageProcessor != getTargetReactor()) {
            throw new IllegalArgumentException("Reactor is not shared");
        }
        messageSource = messageProcessor;
        oldMessage = messageProcessor.getCurrentMessage();
        sourceExceptionHandler = messageProcessor.getExceptionHandler();
        messageProcessor.setCurrentMessage(this);
        messageProcessor.setExceptionHandler(null);
        messageProcessor.messageStartTimeMillis = messageProcessor.scheduler.currentTimeMillis();
        try {
            return syncRequest.processSyncRequest();
        } catch (final Exception e) {
            final ExceptionHandler<RESPONSE_TYPE> currentExceptionHandler = messageProcessor
                    .getExceptionHandler();
            if (currentExceptionHandler == null) {
                throw e;
            }
            return currentExceptionHandler.processException(e);
        } finally {
            messageProcessor.messageStartTimeMillis = messageProcessor.scheduler.currentTimeMillis();
            messageProcessor.setCurrentMessage(oldMessage);
            messageProcessor.setExceptionHandler(sourceExceptionHandler);
        }
    }
}
