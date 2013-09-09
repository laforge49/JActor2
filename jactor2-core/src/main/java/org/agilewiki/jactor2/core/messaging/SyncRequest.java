package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

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
     */
    abstract public RESPONSE_TYPE processSyncRequest()
            throws Exception;

    @Override
    protected void processRequestMessage() throws Exception {
        processObjectResponse(processSyncRequest());
    }

    public RESPONSE_TYPE local(final MessageProcessor _source) throws Exception {
        return null;
    }
}
