package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.Actor;

//An interface for actors which process StatusUpdate events.
public interface StatusListener extends Actor {
    //Process a StatusUpdate event.
    void statusUpdate(final StatusUpdate _statusUpdate) throws Exception;
}
