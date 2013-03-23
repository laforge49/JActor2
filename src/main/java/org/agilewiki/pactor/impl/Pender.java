package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.Message;
import org.agilewiki.pactor.MessageSource;

import java.util.concurrent.Semaphore;

final class Pender implements MessageSource {
    private final Semaphore done = new Semaphore(0);
    private transient Object result;

    public Object pend() throws Exception {
        done.acquire();
        if (result instanceof Exception)
            throw (Exception) result;
        if (result instanceof Error)
            throw (Error) result;
        return result;
    }

    @Override
    public void incomingResponse(final Message message) {
        this.result = message.getResponse();
        done.release();
    }
}
