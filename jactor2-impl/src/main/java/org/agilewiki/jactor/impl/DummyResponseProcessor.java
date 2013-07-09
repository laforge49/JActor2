package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.ResponseProcessor;

final class DummyResponseProcessor implements ResponseProcessor<Object> {
    public static final DummyResponseProcessor SINGLETON = new DummyResponseProcessor();

    private DummyResponseProcessor() {
    }

    @Override
    public void processResponse(final Object response) {
    }
}
