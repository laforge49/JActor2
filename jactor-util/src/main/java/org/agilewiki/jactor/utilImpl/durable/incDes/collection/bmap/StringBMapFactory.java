package org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor.util.durable.incDes.*;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.smap.StringSMapFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates StringBMap's.
 */
public class StringBMapFactory extends FactoryImpl {
    private final static int NODE_CAPACITY = 28;

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        registerFactory(_factoryLocator, PAMap.STRING_PASTRING_MAP, PAString.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_BYTES_MAP, Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_BOX_MAP, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_PALONG_MAP, PALong.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.STRING_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);
    }

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String valueType) {
        UnionImpl.registerFactory(_factoryLocator,
                "U." + actorType, "LM." + actorType, "IM." + actorType);

        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new StringBMapFactory(
                actorType, valueType, true, true));
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new StringBMapFactory(
                "IN." + actorType, valueType, false, false));

        StringSMapFactory.registerFactory(
                _factoryLocator, "LM." + actorType, valueType, NODE_CAPACITY);
        StringSMapFactory.registerFactory(
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
     * @param isRoot    Create a root node when true.
     * @param auto      Define the node as a leaf when true.
     */
    protected StringBMapFactory(String jidType, String valueType,
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
    protected StringBMap instantiateActor() {
        return new StringBMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public StringBMap newSerializable(Mailbox mailbox, Ancestor parent)
            throws Exception {
        StringBMap imj = (StringBMap) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        imj.valueFactory = fl.getFactory(valueType);
        imj.nodeCapacity = NODE_CAPACITY;
        imj.isRoot = isRoot;
        imj.init();
        if (auto)
            imj.setNodeLeaf();
        return imj;
    }
}
