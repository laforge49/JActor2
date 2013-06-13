package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class SignalImpl<TARGET extends Actor>
        extends MessageImpl<TARGET> implements Signal<TARGET> {

    SignalImpl(final Message _message, final TARGET _targetActor) {
        super(_message, _targetActor);
    }

    @Override
    public void run() {
        TARGET targetActor = getTargetActor();
        try {
            targetActor.getSemaphore().acquire();
        } catch (InterruptedException e) {
            return;
        }
        if (isSameThread())
            getSourceSemaphore().release();
        Message message = null;
        try {
            message = processSignal(targetActor);
        } catch(Throwable e1) {
            ExceptionHandler exceptionHandler = getExceptionHandler();
            if (exceptionHandler == null)
                e1.printStackTrace();
            else
                try {
                    exceptionHandler.processException(e1);
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
        }
        if (message == null)
            targetActor.getSemaphore().release();
        else
            message.run();
    }
}
