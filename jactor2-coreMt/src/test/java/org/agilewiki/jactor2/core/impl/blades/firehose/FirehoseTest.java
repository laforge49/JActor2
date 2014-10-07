package org.agilewiki.jactor2.core.impl.blades.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        new Plant();
        try {
            DataProcessor next = new EndStage();
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            next = new NullStage(next);
            new FirstStage(next, 100, 10);
            try {
                Thread.sleep(60000);
            } catch (final Exception ex) {
            }
        } finally {
            Plant.close();
        }
    }
}
