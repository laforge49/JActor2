package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.util.UtilMailboxFactory;

public class FirehoseTest extends TestCase {
    public void test1() throws Exception {
        long count = 100000;
//        long count = 100000000;
        UtilMailboxFactory testMBF = new UtilMailboxFactory();
        try {
            Generate generate = new Generate(testMBF.createFirehoseMailbox(), count);
            Terminate terminate = new Terminate(
                    testMBF.createFirehoseMailbox(),
                    count,
                    Thread.currentThread());
            Stage[] stages = {generate, terminate};
            Firehose firehose = new Firehose(testMBF, stages);
            generate.setFirehose(firehose);
            long t0 = System.currentTimeMillis();
            firehose.start();
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            if (d > 0)
                System.out.println("per second = " + (count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class Generate extends StageBase {

    private long count;

    private long ndx;

    private Firehose firehose;

    public Generate(FirehoseMailbox _mailbox, final long _count) {
        super(_mailbox);
        count = _count;
    }

    public void setFirehose(final Firehose _firehose) {
        firehose = _firehose;
    }

    @Override
    public Object process(final Engine _engine, Object data) {
        if (ndx < count) {
            ndx += 1;
            return ndx;
        } else
            firehose.stop();
        return null;
    }
}

class Terminate extends StageBase {

    private long count;

    private Thread thread;

    public Terminate(FirehoseMailbox _mailbox, final long _count, final Thread _thread) {
        super(_mailbox);
        count = _count;
        thread = _thread;
    }

    @Override
    public Object process(Engine _engine, Object data) {
        if (data == null)
            return null;
        long i = (Long) data;
        if (i == count) {
            thread.interrupt();
        }
        return null;
    }
}