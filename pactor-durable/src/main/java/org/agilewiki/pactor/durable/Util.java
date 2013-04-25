package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Actor;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Properties;
import org.agilewiki.pautil.Ancestor;

public class Util {

    public static FactoryLocator getFactoryLocator(final Properties _properties) {
        return (FactoryLocator) _properties.getProperty("factoryLocator");
    }

    public static FactoryLocator getFactoryLocator(final MailboxFactory _mailboxFactory) {
        return getFactoryLocator(_mailboxFactory.getProperties());
    }

    public static FactoryLocator getFactoryLocator(final Mailbox _mailbox) {
        return getFactoryLocator(_mailbox.getMailboxFactory());
    }

    public static FactoryLocator getFactoryLocator(final Actor _actor) {
        return getFactoryLocator(_actor.getMailbox());
    }

    public static PASerializable newJid(Actor actor, String jidType, Mailbox mailbox, Ancestor parent)
            throws Exception {
        return newJid(getFactoryLocator(actor), jidType, mailbox, parent);
    }

    public static PASerializable newJid(FactoryLocator factoryLocator, String jidType, Mailbox mailbox, Ancestor parent)
            throws Exception {
        return factoryLocator.newJid(jidType, mailbox, parent);
    }
}
