package org.agilewiki.jactor3.timings;

import junit.framework.TestCase;
import org.agilewiki.jactor3.*;

import java.util.concurrent.Semaphore;

interface Loop3I extends Actor {
    Message again(final Signal _signal);
}

class Loop3Signal extends SignalImpl<Loop3I> {

    Loop3Signal(final Message _message, final Loop3I _targetActor) {
        super(_message, _targetActor);
    }

    @Override
    public Message processSignal(Loop3I _targetActor) {
        return _targetActor.again(this);
    }
}

public class Loop3Test extends TestCase implements Loop3I {

    private long count = 1;
//    private long count = 10000000;

    private int i;

    private Semaphore semaphore;

    private Loop3Other other;

    private ThreadManager threadManager;

    private Thread thread;

    public void test1() throws Exception {
        thread = Thread.currentThread();
        semaphore = new Semaphore(1, true);
        threadManager = ThreadManagerImpl.newThreadManager(10);
        MainContext mainContext = new MainContext(this);
        other = new Loop3Other(new Semaphore(1, true), threadManager, this);
        long t0 = System.currentTimeMillis();
        new Loop3Signal(mainContext, other).execute();
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
            thread.interrupt();
            return null;
        } else {
            new Loop3Signal(_signal, other).execute();
            return null;
        }
    }

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }

    @Override
    public ThreadManager getThreadManager() {
        return threadManager;
    }
}

class Loop3Other extends ActorBase implements Loop3I {

    private Loop3Test testActor;

    public Loop3Other(final Semaphore _semaphore, final ThreadManager _threadManager, final Loop3Test _testActor) {
        super(_semaphore, _threadManager);
        testActor = _testActor;
    }

    @Override
    public Message again(Signal _signal) {
        new Loop3Signal(_signal, testActor).execute();
        return null;
    }
}
