package org.agilewiki.jactor2.core.messaging;

/**
 * Used to flag a message as a 1-way message.
 */
final public class SignalResponseProcessor implements ResponseProcessor<Void> {
    /**
     * The class singleton.
     */
    public static final SignalResponseProcessor SINGLETON = new SignalResponseProcessor();

    /**
     * Restrict the class to creating only the class singleton.
     */
    private SignalResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
        throw new UnsupportedOperationException();
    }
}
