package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//An actor which logs StatusUpdate events.
public class StatusLogger extends ActorBase implements StatusListener {
    //The logger.
    protected final Logger logger = LoggerFactory.getLogger(StatusLogger.class);

    //Create a StatusLogger.
    public StatusLogger(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    //Logs the revised status.
    @Override
    public void statusUpdate(final StatusUpdate _statusUpdate) {
        logger.info("new status: " + _statusUpdate.newStatus);
    }
}
