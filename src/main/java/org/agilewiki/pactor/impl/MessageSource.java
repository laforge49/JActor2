package org.agilewiki.pactor.impl;

public interface MessageSource {
    void incomingResponse(final Message message);
}
