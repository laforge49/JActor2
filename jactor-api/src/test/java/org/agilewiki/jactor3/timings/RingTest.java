package org.agilewiki.jactor3.timings;

import junit.framework.TestCase;
import org.agilewiki.jactor3.*;

import java.util.concurrent.Semaphore;

interface RingI extends Actor {
    Message again(final Signal _signal, long i);
}

class RingSignal extends SignalImpl<RingI> {

    long i;

    RingSignal(final Message _message, final RingI _targetActor, final long _i) {
        super(_message, _targetActor, true);
        i = _i;
    }

    @Override
    public Message processSignal(RingI _targetActor) {
        return _targetActor.again(this, i);
    }
}

class RingOther extends ActorBase implements RingI {

    private RingI next;

    RingOther(final Semaphore _semaphore, final ThreadManager _threadManager, final RingI _next) {
        super(_semaphore, _threadManager);
        next = _next;
    }

    @Override
    public Message again(final Signal _signal, final long _i) {
        return new RingSignal(_signal, next, _i);
    }
}

public class RingTest extends TestCase implements RingI {

//    private long count = 100000;
    private long count = 1;

    private long i;

    private Semaphore semaphore;

    private RingI next;

    private ThreadManager threadManager;

    private Thread thread;

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }

    @Override
    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public void test1() throws Exception {
        System.gc();
        threadManager = ThreadManagerImpl.newThreadManager(10);
        try {
            thread = Thread.currentThread();
            semaphore = new Semaphore(1);
            MainContext mainContext = new MainContext(this);
            next = new RingOther(new Semaphore(1), threadManager, this);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            next = new RingOther(new Semaphore(1), threadManager, next);
            long t0 = System.currentTimeMillis();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            new RingSignal(mainContext, this, 0).execute();
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("elapsed time (ms): " + d);
            if (d > 0)
                System.out.println("messages per second: " + (10 * count * 1000 / d));
        } finally {
            threadManager.close();
        }
    }

    public Message again(final Signal _signal, long _i) {
        if (_i >= count) {
            thread.interrupt();
            return null;
        } else if (i < count) {
            i += 1;
            return new RingSignal(_signal, next, i);
        } else
            return null;
    }
}
