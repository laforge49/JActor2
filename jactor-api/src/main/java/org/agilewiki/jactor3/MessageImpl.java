package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class MessageImpl<TARGET extends Actor> implements Message {

    /**
     * The current exception handler, or null.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The semaphore of the actor that sent the message.
     */
    private Semaphore sourceSemaphore;

    /**
     * The actor receiving the message.
     */
    private TARGET targetActor;

    MessageImpl(final Semaphore _sourceSemaphore, final TARGET _targetActor) {
        setSourceSemaphore(_sourceSemaphore);
        setTargetActor(_targetActor);
    }

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
