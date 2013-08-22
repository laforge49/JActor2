package org.agilewiki.jactor2.core.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        JAContext jaContext = new JAContext();
        try {
            DataProcessor next = new EndStage(jaContext);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            next = new NullStage(jaContext, next);
            new FirstStage(jaContext, next, 10000000, 10);
            try {
                Thread.sleep(60000);
            } catch (Exception ex) {
            }
        } finally {
            jaContext.close();
        }
    }
}
