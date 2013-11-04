package org.agilewiki.jactor2.util;

import junit.framework.TestCase;

public class NamedTest extends TestCase {
    public void test() throws Exception {
        final NamedBase a = new NamedBase();
        a.setName("foo");
        final String nm = a.getName();
        assertEquals("foo", nm);
    }
}
