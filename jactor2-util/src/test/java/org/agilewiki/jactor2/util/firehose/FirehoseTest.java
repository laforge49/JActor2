package org.agilewiki.jactor2.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        DefaultMailboxFactory mailboxFactory = new DefaultMailboxFactory();
        try {
            DataProcessor next = new EndStage(mailboxFactory);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            next = new NullStage(mailboxFactory, next);
            new FirstStage(mailboxFactory, next, 10000000, 10);
            try {
                Thread.sleep(60000);
            } catch (Exception ex) {
            }
        } finally {
            mailboxFactory.close();
        }
    }
}
