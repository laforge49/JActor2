package org.agilewiki.pactor.impl;

interface MessageSource {
    void incomingResponse(final Message message);
}
