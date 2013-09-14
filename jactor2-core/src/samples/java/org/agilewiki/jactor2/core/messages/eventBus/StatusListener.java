package org.agilewiki.jactor2.core.messages.eventBus;

import org.agilewiki.jactor2.core.blades.Blade;

//An interface for blades which process StatusUpdate events.
public interface StatusListener extends Blade {
    //Process a StatusUpdate event.
    void statusUpdate(final StatusUpdate _statusUpdate) throws Exception;
}
