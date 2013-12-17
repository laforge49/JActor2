package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.plant.PoolThread;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

abstract public class SyncRequest<RESPONSE_TYPE> extends
        RequestBase<RESPONSE_TYPE> {

    /**
     * Process the request immediately.
     *
     * @param _source         The targetReactor on whose thread this method was invoked and which
     *                        must be the same as the targetReactor of the target.
     * @param _syncRequest    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     * @return The value returned by the target blades.
     */
    public static <RESPONSE_TYPE> RESPONSE_TYPE doLocal(final ReactorImpl _source,
            final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return _syncRequest.doLocal(_source);
    }

    /**
     * Create a SyncRequest.
     *
     * @param _targetReactor The targetReactor where this SyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Reactor _targetReactor) {
        super(_targetReactor);
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread
     * when the SyncRequest is dequeued from the target inbox for processing.
     *
     * @return The value returned by the target blades.
     */
    abstract protected RESPONSE_TYPE processSyncRequest() throws Exception;

    @Override
    protected void processRequestMessage() throws Exception {
        processObjectResponse(processSyncRequest());
    }

    /**
     * Process the request immediately.
     *
     * @param _source The targetReactor on whose thread this method was invoked and which
     *                must be the same as the targetReactor of the target.
     * @return The value returned by the target blades.
     */
    private RESPONSE_TYPE doLocal(final ReactorImpl _source) throws Exception {
        use();
        final ReactorImpl messageProcessor = ((Reactor) _source).asReactorImpl();
        if (PlantImpl.DEBUG) {
            if (messageProcessor instanceof ThreadBoundReactor) {
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
            return processSyncRequest();
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
