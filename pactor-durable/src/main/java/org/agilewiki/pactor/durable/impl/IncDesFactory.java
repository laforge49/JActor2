package org.agilewiki.pactor.durable.impl;

import org.agilewiki.pactor.durable.FactoryLocator;
import org.agilewiki.pactor.durable.IncDes;

/**
 * Creates a IncDesImpl.
 */
public class IncDesFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator factoryLocator)
            throws Exception {
        factoryLocator.registerJidFactory(new IncDesFactory());
    }

    /**
     * Create a JLPCActorFactory.
     */
    protected IncDesFactory() {
        super(IncDes.FACTORY_NAME);
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected IncDesImpl instantiateActor() throws Exception {
        return new IncDesImpl();
    }
}
