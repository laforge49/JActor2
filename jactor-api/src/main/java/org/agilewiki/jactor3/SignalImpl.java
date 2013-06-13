package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

abstract public class SignalImpl<TARGET extends Actor>
        extends MessageImpl<TARGET> implements Signal<TARGET> {

    SignalImpl(final Semaphore _sourceSemaphore, final TARGET _targetActor) {
        super(_sourceSemaphore, _targetActor);
    }

    @Override
    public void run() {
        TARGET targetActor = getTargetActor();
        try {
            targetActor.getSemaphore().acquire();
        } catch (InterruptedException e) {
            return;
        }
        getSourceSemaphore().release();
        Message message = processSignal(targetActor);
        if (message == null)
            targetActor.getSemaphore().release();
        else
            message.run();
    }
}
