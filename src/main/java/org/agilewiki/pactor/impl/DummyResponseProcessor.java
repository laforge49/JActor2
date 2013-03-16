package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessor;

final class DummyResponseProcessor extends ResponseProcessor<Object> {
    public static final DummyResponseProcessor SINGLETON = new DummyResponseProcessor();

    private DummyResponseProcessor() {
    }

    @Override
    public void processResponse(final Object response) {
    }
}
