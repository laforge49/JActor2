package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;

/**
 * Creates ListJids.
 */
public class SListFactory extends FactoryImpl {

    public static void registerFactory(final FactoryLocator factoryLocator,
            final String actorType, final String valueType)
            throws FactoryLocatorClosedException {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String valueType,
            final int initialCapacity) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new SListFactory(actorType, valueType,
                        initialCapacity));
    }

    private final String entryType;
    private final int initialCapacity;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType         The jid type.
     * @param entryType       The entry type.
     * @param initialCapacity The initial capacity.
     */
    protected SListFactory(final String jidType, final String entryType,
            final int initialCapacity) {
        super(jidType);
        this.entryType = entryType;
        this.initialCapacity = initialCapacity;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected SList instantiateBlade() {
        return new SList();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public SList newSerializable(final Reactor reactor, final Ancestor parent)
            throws Exception {
        final SList lj = (SList) super.newSerializable(reactor, parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        lj.entryFactory = fl.getFactory(entryType);
        lj.initialCapacity = initialCapacity;
        return lj;
    }
}
