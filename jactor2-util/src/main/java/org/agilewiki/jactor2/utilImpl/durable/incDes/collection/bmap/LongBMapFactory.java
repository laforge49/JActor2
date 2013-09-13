package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap.LongSMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates LongBMap's.
 */
public class LongBMapFactory extends FactoryImpl {
    private final static int NODE_CAPACITY = 28;

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String valueType) throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator,
                "U." + actorType, "LM." + actorType, "IM." + actorType);

        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new LongBMapFactory(
                actorType, valueType, true, true));
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new LongBMapFactory(
                "IN." + actorType, valueType, false, false));

        LongSMapFactory.registerFactory(
                _factoryLocator, "LM." + actorType, valueType, NODE_CAPACITY);
        LongSMapFactory.registerFactory(
                _factoryLocator, "IM." + actorType, "IN." + actorType, NODE_CAPACITY);
    }

    private String valueType;
    private boolean isRoot = true;
    private boolean auto = true;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     */
    protected LongBMapFactory(String jidType, String valueType,
                              boolean isRoot, boolean auto) {
        super(jidType);
        this.valueType = valueType;
        this.isRoot = isRoot;
        this.auto = auto;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected LongBMap instantiateBlade() {
        return new LongBMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public LongBMap newSerializable(Reactor reactor, Ancestor parent)
            throws Exception {
        LongBMap imj = (LongBMap) super.newSerializable(reactor, parent);
        FactoryLocator fl = Durables.getFactoryLocator(reactor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.nodeCapacity = NODE_CAPACITY;
        imj.isRoot = isRoot;
        imj.init();
        if (auto)
            imj.setNodeLeaf();
        return imj;
    }
}
