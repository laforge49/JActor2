package org.agilewiki.jactor2.api;

final public class DummyResponseProcessor implements ResponseProcessor<Object> {
    public static final DummyResponseProcessor SINGLETON = new DummyResponseProcessor();

    private DummyResponseProcessor() {
    }

    @Override
    public void processResponse(final Object response) {
    }
}