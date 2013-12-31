package org.agilewiki.jactor2.core.blades.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        final BasicPlant plant = new BasicPlant();
        try {
            DataProcessor next = new EndStage(plant);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            next = new NullStage(plant, next);
            new FirstStage(plant, next, 100, 10);
            try {
                Thread.sleep(60000);
            } catch (final Exception ex) {
            }
        } finally {
            plant.close();
        }
    }
}
