package org.agilewiki.jactor3;

import junit.framework.TestCase;

import java.util.concurrent.Semaphore;

public class TestA extends TestCase implements Actor {
    public Semaphore semaphore;

    public void test1() throws Exception {
        semaphore = new Semaphore(0);
        MainContext mainContext = new MainContext(this);
        new SignalA(mainContext, new TargetA(new Semaphore(1))).run();
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (Exception ex) {
        }
    }

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }

    @Override
    public ThreadManager getThreadManager() {
        return null;
    }
}

class SignalA extends SignalImpl<TargetA> {

    SignalA(final Message _message, final TargetA _targetActor) {
        super(_message, _targetActor);
    }

    @Override
    public Message processSignal(TargetA _targetActor) {
        return _targetActor.done(this);
    }
}

class TargetA extends ActorBase {

    /**
     * Create an ActorBase.
     *
     * @param _semaphore The semaphore of the actor.
     */
    TargetA(Semaphore _semaphore) {
        super(_semaphore);
    }

    public Message done(final Signal _signal) {
        Thread.currentThread().interrupt();
        return null;
    }
}
