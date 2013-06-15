package org.agilewiki.jactor3.timings;

import junit.framework.TestCase;
import org.agilewiki.jactor3.*;

import java.util.concurrent.Semaphore;

interface Loop2I extends Actor {
    Message again(final Signal _signal);
}

class Loop2Signal extends SignalImpl<Loop2I> {

    Loop2Signal(final Message _message, final Loop2I _targetActor) {
        super(_message, _targetActor);
    }

    @Override
    public Message processSignal(Loop2I _targetActor) {
        return _targetActor.again(this);
    }
}

public class Loop2Test extends TestCase implements Loop2I {

//    private long count = 100000000;
    private long count = 1;

    private int i;

    private Semaphore semaphore;

    private Loop2Other other;

    public void test1() throws Exception {
        System.gc();
        semaphore = new Semaphore(0, true);
        MainContext mainContext = new MainContext(this);
        other = new Loop2Other(new Semaphore(1, true), this);
        long t0 = System.currentTimeMillis();
        new Loop2Signal(mainContext, other).run();
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (Exception ex) {
        }
        long t1 = System.currentTimeMillis();
        long d = t1 - t0;
        System.out.println("elapsed time (ms): " + d);
        if (d > 0)
            System.out.println("messages per second: " + (2 * count * 1000/d));
    }

    public Message again(final Signal _signal) {
        i += 1;
        if (i >= count) {
            Thread.currentThread().interrupt();
            return null;
        } else
            return new Loop2Signal(_signal, other);
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

class Loop2Other extends ActorBase implements Loop2I {

    private Loop2Test testActor;

    public Loop2Other(final Semaphore _semaphore, final Loop2Test _testActor) {
        super(_semaphore);
        testActor = _testActor;
    }

    @Override
    public Message again(Signal _signal) {
        return new Loop2Signal(_signal, testActor);
    }
}
