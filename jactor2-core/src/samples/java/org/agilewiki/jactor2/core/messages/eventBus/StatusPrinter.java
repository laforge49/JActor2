package org.agilewiki.jactor2.core.messages.eventBus;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

//An blade which prints status logger events.
public class StatusPrinter extends BladeBase implements StatusListener {

    //Create an isolation StatusPrinter. (Isolation because the print mayblock the thread.)
    public StatusPrinter(final Facility _facility) throws Exception {
        Reactor reactor = new IsolationReactor(_facility);
        initialize(reactor);
    }

    //Prints the revised status.
    @Override
    public void statusUpdate(final StatusUpdate _statusUpdate) {
        System.out.println("new status: " + _statusUpdate.newStatus);
    }
}
