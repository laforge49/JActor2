package org.agilewiki.jactor2.core.firehose;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class FirehoseTest extends TestCase {
    public void test() throws Exception {
        System.gc();
        ModuleContext moduleContext = new ModuleContext();
        try {
            DataProcessor next = new EndStage(moduleContext);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            next = new NullStage(moduleContext, next);
            new FirstStage(moduleContext, next, 1, 10);
            try {
                Thread.sleep(60000);
            } catch (Exception ex) {
            }
        } finally {
            moduleContext.close();
        }
    }
}
