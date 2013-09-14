package org.agilewiki.jactor2.core.messages.eventBus;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.EventBus;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class EventBusSample {

    public static void main(final String[] _args) throws Exception {
        //Create a module facility.
        Facility facility = new Facility();
        try {
            //Create a status logger blade.
            StatusLogger statusLogger =
                    new StatusLogger(new NonBlockingReactor(facility));

            //Create a status printer blade.
            StatusPrinter statusPrinter = new StatusPrinter(facility);

            //Define an event bus for StatusListener blades.
            EventBus<StatusListener> eventBus =
                    new EventBus<StatusListener>(new NonBlockingReactor(facility));

            //Add statusLogger and statusPrinter to the subscribers of the event bus.
            eventBus.subscribeAReq(statusLogger).call();
            eventBus.subscribeAReq(statusPrinter).call();

            //Send a status update to all subscribers.
            eventBus.publishAReq(new StatusUpdate("started")).call();

            //Send a status update to all subscribers.
            eventBus.publishAReq(new StatusUpdate("stopped")).call();
        } finally {
            //Close the module facility.
            facility.close();
        }
    }
}
