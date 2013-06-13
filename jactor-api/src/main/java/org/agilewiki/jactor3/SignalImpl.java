package org.agilewiki.jactor3;

abstract public class SignalImpl<TARGET extends Actor>
        extends MessageImpl<TARGET> implements Signal<TARGET> {

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
