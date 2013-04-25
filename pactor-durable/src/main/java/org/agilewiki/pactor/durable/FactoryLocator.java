package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pautil.Ancestor;

/**
 * Defines actor types and instantiating
 */
public interface FactoryLocator extends Ancestor {
    MailboxFactory getMailboxFactory();

    /**
     * Bind an actor type to a Class.
     *
     * @param jidType The jid type.
     * @param clazz   The class of the actor.
     */
    void defineJidType(String jidType, Class clazz)
            throws Exception;

    /**
     * Register an actor factory.
     *
     * @param factoryImpl An actor factory.
     */
    void registerJidFactory(Factory factoryImpl)
            throws Exception;

    /**
     * Returns the requested actor factory.
     *
     * @param jidType The jid type.
     * @return The registered actor factory.
     */
    Factory getFactory(String jidType)
            throws Exception;

    Factory _getActorFactory(String actorType)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param jidType The jid type.
     * @return The new jid.
     */
    PASerializable newJid(String jidType)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param jidType The jid type.
     * @param mailbox A mailbox which may be shared with other actors, or null.
     * @return The new actor.
     */
    PASerializable newJid(String jidType, Mailbox mailbox)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param jidType The jid type.
     * @param mailbox A mailbox which may be shared with other actors, or null.
     * @param parent  The parent actor to which unrecognized requests are forwarded, or null.
     * @return The new actor.
     */
    PASerializable newJid(String jidType, Mailbox mailbox, Ancestor parent)
            throws Exception;
}
