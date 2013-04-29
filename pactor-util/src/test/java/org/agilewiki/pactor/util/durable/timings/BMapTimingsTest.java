package org.agilewiki.pactor.util.durable.timings;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.util.durable.*;

public class BMapTimingsTest extends TestCase {
    public void test1() throws Exception {

        int s = 1000000;
        int r = 1000;

        //map size = 1,000
        //repetitions = 1,000
        //total run time (milliseconds) =  41
        //time per update (microseconds) = 41

        //map size = 10,000
        //repetitions = 10,000
        //total run time (milliseconds) = 590
        //time per update (microseconds) = 59

        //map size = 100,000
        //repetitions = 10,000
        //total run time (milliseconds) = 4724
        //time per update (microseconds) = 472

        //map size = 1,000,000
        //repetitions = 1,000
        //total run time (milliseconds) =  9871
        //time per update (microseconds) = 9871

        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAMap<Integer, PAInteger> m1 = (PAMap) Durables.newSerializable(mailboxFactory, PAMap.INTEGER_PAINTEGER_BMAP);
            int i = 0;
            while (i < s) {
                m1.kMake(i);
                PAInteger ij0 = m1.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            m1.getSerializedBytes();
            int j = 0;
            i = s / 2;
            Mailbox mailbox = mailboxFactory.createMailbox();
            long t0 = System.currentTimeMillis();
            while (j < r) {
                PAMap<Integer, PAInteger> m2 = (PAMap) m1.copy(mailbox);
                PAInteger ij0 = m1.kGet(i);
                ij0.setValue(-i);
                m2.getSerializedBytes();
                j += 1;
            }
            long t1 = System.currentTimeMillis();
            System.out.println("map size = " + s);
            System.out.println("repetitions = " + r);
            long rt = t1 - t0;
            System.out.println("total run time (milliseconds) = " + rt);
            long tpu = rt * 1000L / r;
            System.out.println("time per update (microseconds) = " + tpu);
        } finally {
            mailboxFactory.close();
        }
    }
}
