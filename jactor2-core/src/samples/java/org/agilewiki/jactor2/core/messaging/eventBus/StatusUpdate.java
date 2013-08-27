package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.messaging.Event;

//An event sent to StatusListener actors.
public class StatusUpdate extends Event<StatusListener> {
    //The revised status.
    private final String newStatus;

    //Create a StatusUpdate event.
    public StatusUpdate(final String _newStatus) {
        newStatus = _newStatus;
    }

    //Invokes the statusUpdate method on a StatusListener actor with the revised status.
    @Override
    public void processEvent(StatusListener _targetActor) throws Exception {
        _targetActor.statusUpdate(newStatus);
    }
}
