package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//An actor which logs StatusUpdate events.
public class StatusLogger extends ActorBase implements StatusListener {
    //The logger.
    protected final Logger logger = LoggerFactory.getLogger(StatusLogger.class);

    //Create a StatusLogger.
    public StatusLogger(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Logs the revised status.
    @Override
    public void statusUpdate(final StatusUpdate _statusUpdate) {
        logger.info("new status: " + _statusUpdate.newStatus);
    }
}
