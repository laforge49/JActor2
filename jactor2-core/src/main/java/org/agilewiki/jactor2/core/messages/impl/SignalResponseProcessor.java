package org.agilewiki.jactor2.core.messages.impl;

import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

/**
 * Used to flag a message as a signal. The singleton serves as a flag,
 * so no response is ever returned and consequently the processAsyncResponse method
 * should never actually be invoked.
 */
public final class SignalResponseProcessor implements
        AsyncResponseProcessor<Void> {
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
