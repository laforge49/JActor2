package org.agilewiki.pactor.util.durable;

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
     * Returns the requested actor factory.
     *
     * @param _name The jid type.
     * @return The registered actor factory.
     */
    Factory getFactory(final String _name);
}
