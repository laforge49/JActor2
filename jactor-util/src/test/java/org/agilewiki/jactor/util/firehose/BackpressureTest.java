package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class BackpressureTest extends TestCase {
    public void test1() throws Exception {
        long count = 10;
        int threads = 8;
        MailboxFactory testMBF = new DefaultMailboxFactoryImpl();
        try {
            Backpressure backpressure = new Backpressure(count);
            Passer passer1 = new Passer();
            Passer passer2 = new Passer();
            Passer passer3 = new Passer();
            Passer passer4 = new Passer();
            Passer passer5 = new Passer();
            Passer passer6 = new Passer();
            TerminateB terminate = new TerminateB(count, Thread.currentThread());
            long t0 = System.currentTimeMillis();
            int i = 0;
            while (i < threads) {
                new Engine(testMBF,
                        backpressure,
                        passer1,
                        passer2,
                        passer3,
                        passer4,
                        passer5,
                        passer6,
                        terminate);
                i += 1;
            }
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("per second = " + (7 * count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class Backpressure extends StageBase {

    private long count;

    private long ndx;

    public Backpressure(final long _count) {
        count = _count;
    }

    @Override
    public Object process(final Engine _engine, Object data) {
        int s = 100000;
        List<Long> lst = new ArrayList<Long>(s);
        while (ndx < count && lst.size() < s) {
            ndx += 1;
            lst.add(ndx);
        }
        return lst;
    }
}
