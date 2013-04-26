package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pautil.Ancestor;

/**
 * Defines actor types and instantiating
 */
public interface FactoryLocator extends Ancestor {
    MailboxFactory getMailboxFactory();

    String getLocation();

    String getLocatorKey();

    /**
     * Bind an actor type to a Class.
     *
     * @param _factoryName The jid type.
     * @param clazz   The class of the actor.
     */
    void defineJidType(final String _factoryName, Class clazz)
            throws Exception;

    /**
     * Register an actor factory.
     *
     * @param _factoryImpl An actor factory.
     */
    void registerJidFactory(final Factory _factoryImpl)
            throws Exception;

    /**
     * Returns the requested actor factory.
     *
     * @param _factoryName The jid type.
     * @return The registered actor factory.
     */
    Factory getFactory(final String _factoryName)
            throws Exception;

    Factory _getActorFactory(final String _factoryName)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param _factoryName The jid type.
     * @return The new jid.
     */
    PASerializable newJid(final String _factoryName)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param _factoryName The jid type.
     * @param _mailbox A mailbox which may be shared with other actors, or null.
     * @return The new actor.
     */
    PASerializable newJid(final String _factoryName, final Mailbox _mailbox)
            throws Exception;

    /**
     * Creates a new actor.
     *
     * @param _factoryName The jid type.
     * @param _mailbox A mailbox which may be shared with other actors, or null.
     * @param _parent  The parent actor to which unrecognized requests are forwarded, or null.
     * @return The new actor.
     */
    PASerializable newSerializable(final String _factoryName, final Mailbox _mailbox, final Ancestor _parent)
            throws Exception;
}
