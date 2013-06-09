package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class FirehoseBTest extends TestCase {
    public void test1() throws Exception {
        long count = 100000000;
        MailboxFactory testMBF = new DefaultMailboxFactoryImpl();
        try {
            GenerateB generate = new GenerateB(count);
            TerminateB terminate = new TerminateB(count, Thread.currentThread());
            long t0 = System.currentTimeMillis();
            new Engine(testMBF, generate, terminate);
            try {
                Thread.sleep(1000*60*60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("per second = " + (count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class GenerateB extends StageBase {

    private long count;

    private long ndx;

    public GenerateB(final long _count) {
        super(false);
        count = _count;
    }

    @Override
    public Object process(final Mailbox _mailbox, Object data) {
        int s = 100000;
        List<Long> lst = new ArrayList<Long>(s);
        while (ndx < count && lst.size() < s) {
            ndx += 1;
            lst.add(ndx);
        }
        return lst;
    }
}

class TerminateB extends StageBase {

    private long count;

    private Thread thread;

    public TerminateB(final long _count, final Thread _thread) {
        super(true);
        count = _count;
        thread = _thread;
    }

    @Override
    public Object process(Mailbox _mailbox, Object data) {
        List<Long> lst = (List<Long>) data;
        if (lst.size() == 0)
            return null;
        long i = lst.get(lst.size() - 1);
        if (i == count) {
            thread.interrupt();
        }
        return null;
    }
}
