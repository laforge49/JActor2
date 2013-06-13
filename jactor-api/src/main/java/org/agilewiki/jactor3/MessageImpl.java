package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class MessageImpl<TARGET extends Actor> implements Message {

    private ExceptionHandler exceptionHandler;

    private Semaphore sourceSemaphore;

    private TARGET targetActor;

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(final ExceptionHandler _exceptionHandler) {
        exceptionHandler = _exceptionHandler;
    }

    @Override
    public void execute(final Message _Message) {
        throw new UnsupportedOperationException();
    }

    public void setSourceSemaphore(final Semaphore _sourceSemaphore) {
        sourceSemaphore = _sourceSemaphore;
    }

    public Semaphore getSourceSemaphore() {
        return sourceSemaphore;
    }

    public void setTargetActor(final TARGET _targetActor) {
        targetActor = _targetActor;
    }

    public TARGET getTargetActor() {
        return targetActor;
    }
}
