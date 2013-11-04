package org.agilewiki.jactor2.core.blades.firehose;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        final Plant plant = new Plant();
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
            new FirstStage(plant, next, 1, 10);
            try {
                Thread.sleep(60000);
            } catch (final Exception ex) {
            }
        } finally {
            plant.close();
        }
    }
}
