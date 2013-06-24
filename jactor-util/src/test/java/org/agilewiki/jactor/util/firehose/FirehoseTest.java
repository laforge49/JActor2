package org.agilewiki.jactor.util.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor.util.UtilMailboxFactory;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
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
            new FirstStage(mailboxFactory, next, 10, 1);
        } finally {
            mailboxFactory.close();
        }
    }
}
