package org.agilewiki.jactor2.api;

final public class SignalResponseProcessor implements ResponseProcessor<Void> {
    public static final SignalResponseProcessor SINGLETON = new SignalResponseProcessor();

    private SignalResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
    }
}
