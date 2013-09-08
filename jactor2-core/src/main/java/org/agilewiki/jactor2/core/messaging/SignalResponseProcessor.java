package org.agilewiki.jactor2.core.messaging;

/**
 * Used to flag a message as a 1-way message. The singleton serves as a flag,
 * so no response is ever returned and consequently the processAsyncResponse method
 * should never actually be invoked.
 */
final public class SignalResponseProcessor implements AsyncResponseProcessor<Void> {
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
    public void processAsyncResponse(final Void response) {
        throw new UnsupportedOperationException();
    }
}
