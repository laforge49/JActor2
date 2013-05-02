package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.util.Ancestor;

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
