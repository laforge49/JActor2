package org.agilewiki.jactor2.core.messages.eventBus;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.EventBus;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class EventBusSample {

    public static void main(final String[] _args) throws Exception {
        //Create a module facility.
        Plant plant = new Plant();
        try {
            //Create a status logger blade.
            StatusLogger statusLogger =
                    new StatusLogger(new NonBlockingReactor(plant));

            //Create a status printer blade.
            StatusPrinter statusPrinter = new StatusPrinter(plant);

            //Define an event bus for StatusListener blades.
            EventBus<StatusListener> eventBus =
                    new EventBus<StatusListener>(new NonBlockingReactor(plant));

            //Add statusLogger and statusPrinter to the subscribers of the event bus.
            eventBus.subscribeSReq(statusLogger).call();
            eventBus.subscribeSReq(statusPrinter).call();

            //Send a status update to all subscribers.
            eventBus.publishSReq(new StatusUpdate("started")).call();

            //Send a status update to all subscribers.
            eventBus.publishSReq(new StatusUpdate("stopped")).call();
        } finally {
            //Close the module facility.
            plant.close();
        }
    }
}
