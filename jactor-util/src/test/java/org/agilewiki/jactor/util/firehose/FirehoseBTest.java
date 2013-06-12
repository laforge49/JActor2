package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.UtilMailboxFactory;

import java.util.ArrayList;
import java.util.List;

public class FirehoseBTest extends TestCase {
    public void test1() throws Exception {
        long count = 10;
//        long count = 1000000000;
        int threads = 8;
        UtilMailboxFactory testMBF = new UtilMailboxFactory();
        try {
            GenerateB generate = new GenerateB(testMBF.createFirehoseMailbox(), count);
            Passer passer1 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer2 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer3 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer4 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer5 = new Passer(testMBF.createFirehoseMailbox());
            Passer passer6 = new Passer(testMBF.createFirehoseMailbox());
            TerminateB terminate = new TerminateB(
                    testMBF.createFirehoseMailbox(), count, Thread.currentThread());
            Stage[] stages = {generate,
                    passer1,
                    passer2,
                    passer3,
                    passer4,
                    passer5,
                    passer6,
                    terminate};
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
                System.out.println("per second = " + (7 * count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class GenerateB extends StageBase {

    private long count;

    private long ndx;

    private Firehose firehose;

    public GenerateB(FirehoseMailbox _mailbox, final long _count) {
        super(_mailbox);
        count = _count;
    }

    public void setFirehose(final Firehose _firehose) {
        firehose = _firehose;
    }

    @Override
    public Object process(final Engine _engine, Object data) {
        int s = 100000;
        List<Long> lst = new ArrayList<Long>(s);
        while (ndx < count && lst.size() < s) {
            ndx += 1;
            lst.add(ndx);
        }
        if (ndx == count)
            firehose.stop();
        return lst;
    }
}
