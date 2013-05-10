package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.util.Ancestor;

/**
 * Locates factories by factory name or factory key, where the key is in the form
 * factoryName|bundleName|bundleVersion.
 */
public interface FactoryLocator extends Ancestor {
    /**
     * Returns the requested factory.
     *
     * @param _name The factory name or factory key.
     * @return The matching factory.
     * @throws IllegalArgumentException is thrown if there is no matching factory.
     */
    Factory getFactory(final String _name) throws Exception;
}
