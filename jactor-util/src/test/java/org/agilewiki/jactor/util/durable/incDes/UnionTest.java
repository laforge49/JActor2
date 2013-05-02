package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.FactoryLocator;

public class UnionTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            Durables.registerUnionFactory(factoryLocator, "siUnion", JAString.FACTORY_NAME, "siUnion");
            Union siu1 = (Union) Durables.newSerializable(mailboxFactory, "siUnion");
            assertNull(siu1.getValue());
            Mailbox mailbox = mailboxFactory.createMailbox();
            Union siu2 = (Union) siu1.copy(mailbox);
            assertNull(siu2.getValue());
            siu2.setValue(JAString.FACTORY_NAME);
            JAString sj2 = (JAString) siu2.getValue();
            assertNotNull(sj2);
            Union siu3 = (Union) siu2.copy(mailbox);
            JAString sj3 = (JAString) siu3.getValue();
            assertNotNull(sj3);
        } finally {
            mailboxFactory.close();
        }
    }
}
