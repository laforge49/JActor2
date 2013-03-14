package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessor;

public final class DummyResponseProcessor
        extends ResponseProcessor<Object> {
    public final static DummyResponseProcessor singleton = new DummyResponseProcessor();

    private DummyResponseProcessor() {
    }

    @Override
    public void processResponse(Object response) {
    }
}
