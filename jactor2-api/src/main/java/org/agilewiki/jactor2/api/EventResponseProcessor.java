package org.agilewiki.jactor2.api;

final public class EventResponseProcessor implements ResponseProcessor<Void> {
    public static final EventResponseProcessor SINGLETON = new EventResponseProcessor();

    private EventResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
    }
}
