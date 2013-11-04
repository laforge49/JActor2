package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.MapEntryFactory;

/**
 * Creates IntegerSMap's.
 */
public class IntegerSMapFactory extends FactoryImpl {

    public static void registerFactory(final FactoryLocator factoryLocator,
            final String actorType, final String valueType)
            throws FactoryLocatorClosedException {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String valueType,
            final int initialCapacity) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new MapEntryFactory("E." + actorType,
                        JAInteger.FACTORY_NAME, valueType));
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new IntegerSMapFactory(actorType, valueType,
                        initialCapacity));
    }

    private final String valueType;
    private int initialCapacity = 10;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     */
    protected IntegerSMapFactory(final String jidType, final String valueType,
            final int initialCapacity) {
        super(jidType);
        this.valueType = valueType;
        this.initialCapacity = initialCapacity;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected IntegerSMap instantiateBlade() {
        return new IntegerSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public IntegerSMap newSerializable(final Reactor reactor,
            final Ancestor parent) throws Exception {
        final IntegerSMap imj = (IntegerSMap) super.newSerializable(reactor,
                parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
