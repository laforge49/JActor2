package org.agilewiki.jactor2.core.messages.eventBus;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//An blade which logs StatusUpdate events.
public class StatusLogger extends BladeBase implements StatusListener {
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
