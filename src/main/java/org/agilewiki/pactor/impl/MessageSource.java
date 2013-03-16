package org.agilewiki.pactor.impl;

interface MessageSource {
    void incomingResponse(final RequestMessage requestMessage,
            final Object response);
}
