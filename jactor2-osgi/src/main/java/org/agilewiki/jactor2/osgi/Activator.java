package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.util.durable.Durables;

/**
 * Activator for the jactor2-osgi bundle,
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
