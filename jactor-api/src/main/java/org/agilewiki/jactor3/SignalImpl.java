package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class SignalImpl<TARGET extends Actor>
        extends MessageImpl<TARGET> implements Signal<TARGET> {

    /**
     * Acquire next before releasing current.
     */
    private boolean backpressure;

    /**
     * Create a Signal message.
     *
     * @param _message     The message that provides the context.
     * @param _targetActor The target actor.
     */
    public SignalImpl(final Message _message, final TARGET _targetActor) {
        super(_message, _targetActor);
    }

    /**
     * Create a Signal message.
     *
     * @param _message      The message that provides the context.
     * @param _targetActor  The target actor.
     * @param _backpressure When true, backpressure is enabled.
     */
    public SignalImpl(final Message _message,
                      final TARGET _targetActor,
                      final boolean _backpressure) {
        super(_message, _targetActor);
        backpressure = _backpressure;
    }

    @Override
    public Message iteration() {
        Semaphore sourceSemaphore = getSourceSemaphore();
        TARGET targetActor = getTargetActor();
        Semaphore targetSemaphore = targetActor.getSemaphore();
        boolean sameSemaphore = sourceSemaphore == targetSemaphore;
        if (sameSemaphore) {
            if (!isSameThread()) {
                try {
                    targetSemaphore.acquire();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        } else if (backpressure) {
            try {
                targetSemaphore.acquire();
            } catch (InterruptedException e) {
                return null;
            }
            if (isSameThread()) {
                sourceSemaphore.release();
            }
        } else {
            if (isSameThread()) {
                sourceSemaphore.release();
            }
            try {
                targetSemaphore.acquire();
            } catch (InterruptedException e) {
                return null;
            }
        }
        return process(targetActor);
    }

    private Message process(final TARGET _targetActor) {
        Semaphore targetSemaphore = _targetActor.getSemaphore();
        Message message = null;
        try {
            message = processSignal(_targetActor);
        } catch (Throwable e1) {
            ExceptionHandler exceptionHandler = getExceptionHandler();
            if (exceptionHandler == null) {
                targetSemaphore.release();
                e1.printStackTrace();
            } else
                try {
                    exceptionHandler.processException(e1);
                } catch (Throwable e2) {
                    targetSemaphore.release();
                    e2.printStackTrace();
                }
        }
        if (message == null) {
            targetSemaphore.release();
            return null;
        }
        return message;
    }
}
