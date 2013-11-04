package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.Box;
import org.agilewiki.jactor2.util.durable.incDes.Bytes;
import org.agilewiki.jactor2.util.durable.incDes.JABoolean;
import org.agilewiki.jactor2.util.durable.incDes.JADouble;
import org.agilewiki.jactor2.util.durable.incDes.JAFloat;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JALong;
import org.agilewiki.jactor2.util.durable.incDes.JAMap;
import org.agilewiki.jactor2.util.durable.incDes.JAString;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap.StringSMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates StringBMap's.
 */
public class StringBMapFactory extends FactoryImpl {
    private static final int NODE_CAPACITY = 28;

    public static void registerFactories(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        registerFactory(_factoryLocator, JAMap.STRING_JASTRING_MAP,
                JAString.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_BYTES_MAP,
                Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_BOX_MAP, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_JALONG_MAP,
                JALong.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_JAINTEGER_MAP,
                JAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_JAFLOAT_MAP,
                JAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_JADOUBLE_MAP,
                JADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, JAMap.STRING_JABOOLEAN_MAP,
                JABoolean.FACTORY_NAME);
    }

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String valueType)
            throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator, "U." + actorType, "LM."
                + actorType, "IM." + actorType);

        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new StringBMapFactory(actorType, valueType,
                        true, true));
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new StringBMapFactory("IN." + actorType,
                        valueType, false, false));

        StringSMapFactory.registerFactory(_factoryLocator, "LM." + actorType,
                valueType, NODE_CAPACITY);
        StringSMapFactory.registerFactory(_factoryLocator, "IM." + actorType,
                "IN." + actorType, NODE_CAPACITY);
    }

    private final String valueType;
    private boolean isRoot = true;
    private boolean auto = true;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     * @param isRoot    Create a root node when true.
     * @param auto      Define the node as a leaf when true.
     */
    protected StringBMapFactory(final String jidType, final String valueType,
            final boolean isRoot, final boolean auto) {
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
    protected StringBMap instantiateBlade() {
        return new StringBMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public StringBMap newSerializable(final Reactor reactor,
            final Ancestor parent) throws Exception {
        final StringBMap imj = (StringBMap) super.newSerializable(reactor,
                parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.nodeCapacity = NODE_CAPACITY;
        imj.isRoot = isRoot;
        imj.init();
        if (auto) {
            imj.setNodeLeaf();
        }
        return imj;
    }
}
