package org.agilewiki.jactor2.core.messages.impl;

import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

/**
 * Used to flag a request as a 1-way message. The singleton serves as a flag,
 * so no response is ever returned and consequently the processAsyncResponse method
 * should never actually be invoked.
 * This is the AsyncResponseProcessor used when null is passed as the second argument in the send method.
 */
public final class OneWayResponseProcessor implements
        AsyncResponseProcessor<Void> {
    /**
     * The class singleton.
     */
    public static final OneWayResponseProcessor SINGLETON = new OneWayResponseProcessor();

    /**
     * Restrict the class to creating only the class singleton.
     */
    private OneWayResponseProcessor() {
    }

    @Override
    public void processAsyncResponse(final Void response) {
        throw new UnsupportedOperationException();
    }
}
