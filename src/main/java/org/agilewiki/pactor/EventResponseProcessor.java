package org.agilewiki.pactor;

final public class EventResponseProcessor implements ResponseProcessor<Void> {
    public static final EventResponseProcessor SINGLETON = new EventResponseProcessor();

    private EventResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
    }
}
