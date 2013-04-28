package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;

public class UnionTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            Durables.registerUnionFactory(factoryLocator, "siUnion", PAString.FACTORY_NAME, "siUnion");
            Union siu1 = (Union) Durables.newSerializable(mailboxFactory, "siUnion");
            assertNull(siu1.getValue());
            Mailbox mailbox = mailboxFactory.createMailbox();
            Union siu2 = (Union) siu1.copy(mailbox);
            assertNull(siu2.getValue());
            siu2.setValue(PAString.FACTORY_NAME);
            PAString sj2 = (PAString) siu2.getValue();
            assertNotNull(sj2);
            Union siu3 = (Union) siu2.copy(mailbox);
            PAString sj3 = (PAString) siu3.getValue();
            assertNotNull(sj3);
        } finally {
            mailboxFactory.close();
        }
    }
}
