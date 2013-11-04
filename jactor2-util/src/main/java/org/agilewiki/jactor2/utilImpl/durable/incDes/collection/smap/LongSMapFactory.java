package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JALong;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.MapEntryFactory;

/**
 * Creates LongSMap's.
 */
public class LongSMapFactory extends FactoryImpl {

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
                        JALong.FACTORY_NAME, valueType));
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new LongSMapFactory(actorType, valueType,
                        initialCapacity));
    }

    private final String valueType;
    private int initialCapacity = 10;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType         The jid type.
     * @param valueType       The value type.
     * @param initialCapacity The initial capacity.
     */
    protected LongSMapFactory(final String jidType, final String valueType,
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
    protected LongSMap instantiateBlade() {
        return new LongSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public LongSMap newSerializable(final Reactor reactor, final Ancestor parent)
            throws Exception {
        final LongSMap imj = (LongSMap) super.newSerializable(reactor, parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
