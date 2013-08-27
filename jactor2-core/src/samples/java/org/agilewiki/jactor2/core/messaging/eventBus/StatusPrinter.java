package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class StatusPrinter extends ActorBase implements StatusListener {

    public StatusPrinter(final ModuleContext _moduleContext) throws Exception {
        MessageProcessor messageProcessor = new AtomicMessageProcessor(_moduleContext);
        initialize(messageProcessor);
    }

    @Override
    public void statusUpdate(String _newStatus) {
        System.out.println("new status: " + _newStatus);
    }
}
