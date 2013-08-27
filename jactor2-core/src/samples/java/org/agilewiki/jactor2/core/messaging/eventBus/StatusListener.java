package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.Actor;

public interface StatusListener extends Actor {
    void statusUpdate(final String _newStatus) throws Exception;
}
