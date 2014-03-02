package org.agilewiki.jactor2.core.requests.impl;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * Used to flag a message as a 1-way message. The singleton serves as a flag,
 * so no response is ever returned and consequently the processAsyncResponse method
 * should never actually be invoked.
 * This is the AsyncResponseProcessor used null is passed as the AsyncResponseProcessor when send is invoked.
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
