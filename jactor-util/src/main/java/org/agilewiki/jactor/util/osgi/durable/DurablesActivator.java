package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.util.durable.Durables;

public class DurablesActivator extends FactoryLocatorActivator0 {
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
