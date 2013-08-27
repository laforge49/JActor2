package org.agilewiki.jactor2.core.messaging.eventBus;

import org.agilewiki.jactor2.core.messaging.EventBus;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class EventBusSample {

    public static void main(final String[] _args) throws Exception {
        //Create a module context.
        ModuleContext moduleContext = new ModuleContext();
        try {
            //Create a status logger actor.
            StatusLogger statusLogger =
                    new StatusLogger(new NonBlockingMessageProcessor(moduleContext));

            //Create a status printer actor.
            StatusPrinter statusPrinter = new StatusPrinter(moduleContext);

            //Define an event bus for StatusListener actors.
            EventBus<StatusListener> eventBus =
                    new EventBus<StatusListener>(new NonBlockingMessageProcessor(moduleContext));

            //Add statusLogger and statusPrinter to the subscribers of the event bus.
            eventBus.subscribeReq(statusLogger).call();
            eventBus.subscribeReq(statusPrinter).call();

            //Send a status update to all subscribers.
            eventBus.publishReq(new StatusUpdate("started")).call();

            //Send a status update to all subscribers.
            eventBus.publishReq(new StatusUpdate("stopped")).call();
        } finally {
            //Close the module context.
            moduleContext.close();
        }
    }
}
