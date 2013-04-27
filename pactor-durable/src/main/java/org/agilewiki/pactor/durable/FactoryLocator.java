package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;

/**
 * Defines actor types and instantiating
 */
public interface FactoryLocator extends Ancestor {

    String getLocation();

    String getLocatorKey();

    String getDescriptor();

    /**
     * Register an actor factory.
     *
     * @param _factory An actor factory.
     */
    void registerFactory(final Factory _factory)
            throws Exception;

    /**
     * Returns the requested actor factory.
     *
     * @param _name The jid type.
     * @return The registered actor factory.
     */
    Factory getFactory(final String _name);

    Factory _getFactory(final String _name);

    /**
     * Creates a new actor.
     *
     * @param _name The jid type.
     * @param _mailbox A mailbox which may be shared with other actors, or null.
     * @param _parent  The parent actor to which unrecognized requests are forwarded, or null.
     * @return The new actor.
     */
    PASerializable newSerializable(final String _name, final Mailbox _mailbox, final Ancestor _parent);
}
