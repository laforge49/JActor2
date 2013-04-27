package org.agilewiki.pactor.util;

import junit.framework.TestCase;

import org.agilewiki.pactor.api.ActorBase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory1 = new DefaultMailboxFactoryImpl();
        final MailboxFactory mailboxFactory2 = new DefaultMailboxFactoryImpl();
        try {
            PAProperties p1 = new PAProperties(mailboxFactory1, null);
            PAProperties p2 = new PAProperties(mailboxFactory2, p1);
            p1.putProperty("a", "foo");
            p2.putProperty("b", "bar");
            ActorBase z = new ActorBase();
            z.initialize(mailboxFactory2.createMailbox());
            String a = (String) PAProperties.getProperty(z, "a");
            assertEquals("foo", a);
            String b = (String) PAProperties.getProperty(z, "b");
            assertEquals("bar", b);
            String c = (String) PAProperties.getProperty(z, "c");
            assertNull(c);
        } finally {
            mailboxFactory2.close();
            mailboxFactory1.close();
        }
    }
}
