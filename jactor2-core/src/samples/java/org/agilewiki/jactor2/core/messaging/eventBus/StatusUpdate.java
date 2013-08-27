package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.messaging.Event;

public class StatusUpdate extends Event<StatusListener> {
    private final String newStatus;

    public StatusUpdate(final String _newStatus) {
        newStatus = _newStatus;
    }

    @Override
    public void processEvent(StatusListener _targetActor) throws Exception {
        _targetActor.statusUpdate(newStatus);
    }
}
