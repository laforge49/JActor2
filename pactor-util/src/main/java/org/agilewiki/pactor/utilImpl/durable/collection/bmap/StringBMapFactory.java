package org.agilewiki.pactor.utilImpl.durable.collection.bmap;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.collection.smap.StringSMapFactory;
import org.agilewiki.pactor.utilImpl.durable.scalar.vlens.UnionImpl;

/**
 * Creates StringBMap's.
 */
public class StringBMapFactory extends FactoryImpl {
    private final static int NODE_CAPACITY = 28;

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) {
        UnionImpl.registerFactory(factoryLocator,
                "U." + actorType, "LM." + actorType, "IM." + actorType);

        factoryLocator.registerFactory(new StringBMapFactory(
                actorType, valueType, true, true));
        factoryLocator.registerFactory(new StringBMapFactory(
                "IN." + actorType, valueType, false, false));

        StringSMapFactory.registerFactory(
                factoryLocator, "LM." + actorType, valueType, NODE_CAPACITY);
        StringSMapFactory.registerFactory(
                factoryLocator, "IM." + actorType, "IN." + actorType, NODE_CAPACITY);
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
    public StringBMap newSerializable(Mailbox mailbox, Ancestor parent) {
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
