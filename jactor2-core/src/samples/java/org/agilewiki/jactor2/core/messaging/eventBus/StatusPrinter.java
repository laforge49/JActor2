package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

//An actor which prints status logger events.
public class StatusPrinter extends ActorBase implements StatusListener {

    //Create an isolation StatusPrinter. (Isolation because the print mayblock the thread.)
    public StatusPrinter(final Facility _facility) throws Exception {
        MessageProcessor messageProcessor = new IsolationMessageProcessor(_facility);
        initialize(messageProcessor);
    }

    //Prints the revised status.
    @Override
    public void statusUpdate(final StatusUpdate _statusUpdate) {
        System.out.println("new status: " + _statusUpdate.newStatus);
    }
}
