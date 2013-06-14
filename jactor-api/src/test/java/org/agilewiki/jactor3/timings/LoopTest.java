package org.agilewiki.jactor3.timings;

import junit.framework.TestCase;
import org.agilewiki.jactor3.*;

import java.util.concurrent.Semaphore;

public class LoopTest extends TestCase implements Actor {

    private long count = 1;
//    private long count = 1000000000;

    private int i;

    private Semaphore semaphore;

    public void test1() throws Exception {
        semaphore = new Semaphore(0);
        MainContext mainContext = new MainContext(this);
        long t0 = System.currentTimeMillis();
        new LoopSignal(mainContext, this).run();
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (Exception ex) {
        }
        long t1 = System.currentTimeMillis();
        long d = t1 - t0;
        System.out.println("elapsed time (ms): " + d);
        if (d > 0)
            System.out.println("messages per second: " + (count * 1000/d));
    }

    public Message again(final Signal _signal) {
        i += 1;
        if (count == i) {
            Thread.currentThread().interrupt();
            return null;
        } else
            return new LoopSignal(_signal, this);
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

class LoopSignal extends SignalImpl<LoopTest> {

    LoopSignal(final Message _message, final LoopTest _targetActor) {
        super(_message, _targetActor);
    }

    @Override
    public Message processSignal(LoopTest _targetActor) {
        return _targetActor.again(this);
    }
}
