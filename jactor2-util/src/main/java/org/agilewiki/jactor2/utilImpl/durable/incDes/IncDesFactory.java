package org.agilewiki.jactor2.utilImpl.durable.incDes;

import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.IncDes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;

/**
 * Creates a IncDesImpl.
 */
public class IncDesFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator _factoryLocator) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new IncDesFactory());
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
    protected IncDesImpl instantiateActor() {
        return new IncDesImpl();
    }
}
