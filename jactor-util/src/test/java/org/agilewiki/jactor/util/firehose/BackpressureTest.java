package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.util.UtilMailboxFactory;

import java.util.ArrayList;
import java.util.List;

public class BackpressureTest extends TestCase {
    public void test1() throws Exception {
        long count = 1000000000;
//        long count = 100000;
        UtilMailboxFactory testMBF = new UtilMailboxFactory();
        try {
            Backpressure backpressure = new Backpressure(testMBF.createFirehoseMailbox(), count);
            Passer passer1 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer2 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer3 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer4 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer5 = new Passer(testMBF.createFirehoseMailbox());
            Load load2 = new Load(testMBF.createFirehoseMailbox(), 1);
            TerminateB terminate = new TerminateB(
                    testMBF.createFirehoseMailbox(),
                    count,
                    Thread.currentThread());
            Stage[] stages = {backpressure,
                    passer1,
                    passer2,
                    passer3,
                    passer4,
                    passer5,
                    load2,
                    terminate};
            Firehose firehose = new Firehose(testMBF, stages);
            backpressure.setFirehose(firehose);
            long t0 = System.currentTimeMillis();
            firehose.start();
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("passed per second = " + (7 * count * 1000 / d));
            System.out.println("processed per second = " + (count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class Backpressure extends StageBase {

    private long count;

    private long ndx;

    private int sz = 1;

    private Firehose firehose;

    public Backpressure(FirehoseMailbox _mailbox, final long _count) {
        super(_mailbox);
        count = _count;
    }

    public void setFirehose(final Firehose _firehose) {
        firehose = _firehose;
    }

    @Override
    public Object process(final Engine _engine, Object data) {
        List<Long> lst = new ArrayList<Long>(sz);
        while (ndx < count) {
            ndx += 1;
            lst.add(ndx);
            if (_engine.isNextStageAvailable())
                break;
        }
        if (ndx == count)
            firehose.stop();
        sz = lst.size();
        return lst;
    }
}
