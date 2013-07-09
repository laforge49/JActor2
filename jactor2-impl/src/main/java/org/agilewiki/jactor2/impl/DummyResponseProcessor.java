package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.ResponseProcessor;

final class DummyResponseProcessor implements ResponseProcessor<Object> {
    public static final DummyResponseProcessor SINGLETON = new DummyResponseProcessor();

    private DummyResponseProcessor() {
    }

    @Override
    public void processResponse(final Object response) {
    }
}
