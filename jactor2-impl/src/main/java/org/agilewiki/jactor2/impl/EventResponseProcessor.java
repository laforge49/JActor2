package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.ResponseProcessor;

final public class EventResponseProcessor implements ResponseProcessor<Void> {
    public static final EventResponseProcessor SINGLETON = new EventResponseProcessor();

    private EventResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
    }
}
