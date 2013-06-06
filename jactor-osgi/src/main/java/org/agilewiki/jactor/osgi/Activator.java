package org.agilewiki.jactor.osgi;

import org.agilewiki.jactor.util.durable.Durables;

/**
 * Activator for the jactor-osgi bundle,
 * that provides all the pre-defined durable factories.
 */
public class Activator extends FactoryLocatorActivator {

    protected boolean configImports() {
        return false;
    }

    @Override
    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Durables.registerFactories(factoryLocator);
    }
}
