package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.blist;

import org.agilewiki.jactor2.api.Mailbox;
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
    private final static int NODE_CAPACITY = 28;

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String entryType) throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator,
                "U." + actorType, "LL." + actorType, "IL." + actorType);

        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new BListFactory(
                actorType, entryType, true, true));
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new BListFactory(
                "IN." + actorType, entryType, false, false));

        SListFactory.registerFactory(_factoryLocator,
                "LL." + actorType, entryType, NODE_CAPACITY);
        SListFactory.registerFactory(_factoryLocator,
                "IL." + actorType, "IN." + actorType, NODE_CAPACITY);
    }

    private String entryType;
    private boolean isRoot = true;
    private boolean auto = true;

    private BListFactory(String actorType, String entryType,
                         boolean isRoot, boolean auto) {
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
    protected BList instantiateActor() {
        return new BList();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public BList newSerializable(Mailbox mailbox, Ancestor parent)
            throws Exception {
        BList lj = (BList) super.newSerializable(mailbox, parent);
        FactoryLocator f = Durables.getFactoryLocator(mailbox);
        lj.entryFactory = f.getFactory(entryType);
        lj.nodeCapacity = NODE_CAPACITY;
        lj.isRoot = isRoot;
        lj.init();
        if (auto)
            lj.setNodeLeaf();
        return lj;
    }
}
