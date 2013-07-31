package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final JAContext jaContext1 = new JAContext();
        final JAContext jaContext2 = new JAContext();
        try {
            JAProperties p1 = new JAProperties(jaContext1, null);
            JAProperties p2 = new JAProperties(jaContext2, p1);
            p1.putProperty("a", "foo");
            p2.putProperty("b", "bar");
            ActorBase z = new ActorBase();
            z.initialize(jaContext2.createNonBlockingMailbox());
            String a = (String) JAProperties.getProperty(z, "a");
            assertEquals("foo", a);
            String b = (String) JAProperties.getProperty(z, "b");
            assertEquals("bar", b);
            String c = (String) JAProperties.getProperty(z, "c");
            assertNull(c);
        } finally {
            jaContext2.close();
            jaContext1.close();
        }
    }
}
