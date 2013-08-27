package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusLogger extends ActorBase implements StatusListener {
    protected final Logger logger = LoggerFactory.getLogger(StatusLogger.class);

    public StatusLogger(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    @Override
    public void statusUpdate(String _newStatus) {
        logger.info("new status: " + _newStatus);
    }
}
