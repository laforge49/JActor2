package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class MessageImpl<TARGET extends Actor> implements Message<TARGET> {

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

    /**
     * True if message was not sent to another thread.
     */
    private boolean sameThread = true;

    /**
     * Create a message.
     *
     * @param _message        The message that provides the context.
     * @param _targetActor    The target actor.
     */
    MessageImpl(final Message _message, final TARGET _targetActor) {
        setSourceSemaphore(_message.getTargetActor().getSemaphore());
        setTargetActor(_targetActor);
    }

    MessageImpl(final TARGET _targetActor) {
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
    public void execute() {
        sameThread = false;
        ThreadManager threadManager = targetActor.getThreadManager();
        if (threadManager == null)
            throw new UnsupportedOperationException("target actor does not have a thread manager");
        threadManager.execute(this);
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

    @Override
    public TARGET getTargetActor() {
        return targetActor;
    }

    protected boolean isSameThread() {
        return sameThread;
    }
}
