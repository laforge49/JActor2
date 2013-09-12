package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.messaging.EventBus;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

public class EventBusSample {

    public static void main(final String[] _args) throws Exception {
        //Create a module facility.
        Facility facility = new Facility();
        try {
            //Create a status logger actor.
            StatusLogger statusLogger =
                    new StatusLogger(new NonBlockingMessageProcessor(facility));

            //Create a status printer actor.
            StatusPrinter statusPrinter = new StatusPrinter(facility);

            //Define an event bus for StatusListener actors.
            EventBus<StatusListener> eventBus =
                    new EventBus<StatusListener>(new NonBlockingMessageProcessor(facility));

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
