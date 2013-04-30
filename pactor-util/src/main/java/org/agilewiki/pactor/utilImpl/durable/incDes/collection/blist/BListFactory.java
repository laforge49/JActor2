package org.agilewiki.pactor.utilImpl.durable.incDes.collection.blist;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.incDes.*;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.slist.SListFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates ListJids.
 */
public class BListFactory extends FactoryImpl {
    private final static int NODE_CAPACITY = 28;

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        registerFactory(_factoryLocator, PAList.PASTRING_BLIST, PAString.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.BYTES_BLIST, Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.BOX_BLIST, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PALONG_BLIST, PALong.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PAINTEGER_BLIST, PAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PAFLOAT_BLIST, PAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PADOUBLE_BLIST, PADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PABOOLEAN_BLIST, PABoolean.FACTORY_NAME);
    }

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String entryType) {
        UnionImpl.registerFactory(factoryLocator,
                "U." + actorType, "LL." + actorType, "IL." + actorType);

        factoryLocator.registerFactory(new BListFactory(
                actorType, entryType, true, true));
        factoryLocator.registerFactory(new BListFactory(
                "IN." + actorType, entryType, false, false));

        SListFactory.registerFactory(factoryLocator,
                "LL." + actorType, entryType, NODE_CAPACITY);
        SListFactory.registerFactory(factoryLocator,
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
    public BList newSerializable(Mailbox mailbox, Ancestor parent) {
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
