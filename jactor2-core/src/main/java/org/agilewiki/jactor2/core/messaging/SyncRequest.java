package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessorBase;

abstract public class SyncRequest<RESPONSE_TYPE>
        extends RequestBase<RESPONSE_TYPE> {

    /**
     * Create a SyncRequest.
     *
     * @param _targetMessageProcessor The message processor where this SyncRequest Objects is passed for processing.
     *                                The thread owned by this message processor will process this SyncRequest.
     */
    public SyncRequest(MessageProcessor _targetMessageProcessor) {
        super(_targetMessageProcessor);
    }

    /**
     * The processSyncRequest method will be invoked by the target MessageProcessor on its own thread
     * when the SyncRequest is dequeued from the target inbox for processing.
     *
     * @return The value returned by the target actor.
     */
    abstract public RESPONSE_TYPE processSyncRequest()
            throws Exception;

    @Override
    protected void processRequestMessage() throws Exception {
        processObjectResponse(processSyncRequest());
    }

    /**
     * Process the request immediately.
     *
     * @param _source The message processor on whose thread this method was invoked and which
     *                must be the same as the message processor of the target.
     * @return The value returned by the target actor.
     */
    public RESPONSE_TYPE local(final MessageProcessor _source) throws Exception {
        use();
        MessageProcessorBase source = (MessageProcessorBase) _source;
        if (!source.isRunning())
            throw new IllegalStateException(
                    "A valid source message processor can not be idle");
        if (_source != getMessageProcessor())
            throw new IllegalArgumentException("MessageProcessor is not shared");
        messageSource = source;
        oldMessage = source.getCurrentMessage();
        sourceExceptionHandler = source.getExceptionHandler();
        source.setCurrentMessage(this);
        source.setExceptionHandler(null);
        try {
            return processSyncRequest();
        } finally {
            source.setCurrentMessage(oldMessage);
            source.setExceptionHandler(sourceExceptionHandler);
        }
    }
}
