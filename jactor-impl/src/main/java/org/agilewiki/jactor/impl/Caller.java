package org.agilewiki.jactor.impl;

import java.util.concurrent.Semaphore;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.ResponseProcessor;
import org.agilewiki.jactor.api._Request;

final class Caller implements MessageSource {
    private final Semaphore done = new Semaphore(0);
    private transient Object result;

    public Object call() throws Exception {
        done.acquire();
        if (result instanceof Exception)
            throw (Exception) result;
        if (result instanceof Error)
            throw (Error) result;
        return result;
    }

    @Override
    public void incomingResponse(final Message message,
            final JAMailbox responseSource) {
        this.result = message.getResponse();
        done.release();
    }

    @Override
    public boolean buffer(final Message message, final JAMailbox target) {
        return false;
    }

    @Override
    public boolean isRunning() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E, A extends Actor> Message createMessage(final boolean _foreign,
            final MessageQueue inbox, final _Request<E, A> request,
            final A targetActor, final ResponseProcessor<E> responseProcessor) {
        throw new UnsupportedOperationException();
    }
}
