package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.Blade;

//An interface for actors which process StatusUpdate events.
public interface StatusListener extends Blade {
    //Process a StatusUpdate event.
    void statusUpdate(final StatusUpdate _statusUpdate) throws Exception;
}
