package org.agilewiki.pactor;

public interface MessageSource {
    void incomingResponse(final Message message);
}
