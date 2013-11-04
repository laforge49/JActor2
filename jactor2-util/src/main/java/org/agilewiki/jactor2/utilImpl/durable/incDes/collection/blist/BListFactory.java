package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.blist;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist.SListFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates ListJids.
 */
public class BListFactory extends FactoryImpl {
    private static final int NODE_CAPACITY = 28;

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String entryType)
            throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator, "U." + actorType, "LL."
                + actorType, "IL." + actorType);

        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new BListFactory(actorType, entryType, true,
                        true));
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new BListFactory("IN." + actorType, entryType,
                        false, false));

        SListFactory.registerFactory(_factoryLocator, "LL." + actorType,
                entryType, NODE_CAPACITY);
        SListFactory.registerFactory(_factoryLocator, "IL." + actorType, "IN."
                + actorType, NODE_CAPACITY);
    }

    private final String entryType;
    private boolean isRoot = true;
    private boolean auto = true;

    private BListFactory(final String actorType, final String entryType,
            final boolean isRoot, final boolean auto) {
        super(actorType);
        this.entryType = entryType;
        this.isRoot = isRoot;
        this.auto = auto;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected BList instantiateBlade() {
        return new BList();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public BList newSerializable(final Reactor reactor, final Ancestor parent)
            throws Exception {
        final BList lj = (BList) super.newSerializable(reactor, parent);
        final FactoryLocator f = Durables.getFactoryLocator(reactor);
        lj.entryFactory = f.getFactory(entryType);
        lj.nodeCapacity = NODE_CAPACITY;
        lj.isRoot = isRoot;
        lj.init();
        if (auto) {
            lj.setNodeLeaf();
        }
        return lj;
    }
}
