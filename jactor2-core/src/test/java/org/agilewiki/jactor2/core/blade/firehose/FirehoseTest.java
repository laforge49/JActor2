package org.agilewiki.jactor2.core.blade.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        Facility facility = new Facility();
        try {
            DataProcessor next = new EndStage(facility);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            next = new NullStage(facility, next);
            new FirstStage(facility, next, 1, 10);
            try {
                Thread.sleep(60000);
            } catch (Exception ex) {
            }
        } finally {
            facility.close();
        }
    }
}
