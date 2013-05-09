package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.osgi.FactoryLocatorActivator;

public class DurablesActivator extends FactoryLocatorActivator {
    @Override
    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Durables.registerFactories(getFactoryLocator());
        System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*");
        System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*");
        System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*");
        System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*");
    }
}
