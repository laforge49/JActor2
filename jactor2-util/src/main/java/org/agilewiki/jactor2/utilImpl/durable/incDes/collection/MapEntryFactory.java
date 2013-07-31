package org.agilewiki.jactor2.utilImpl.durable.incDes.collection;

import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;

/**
 * Creates map entries.
 */
public class MapEntryFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String keyType,
                                       String valueType) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new MapEntryFactory(
                actorType, keyType, valueType));
    }

    private String keyType;
    private String valueType;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType The jid type.
     */
    public MapEntryFactory(String jidType, String keyType, String valueType) {
        super(jidType);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected MapEntryImpl instantiateActor() {
        return new MapEntryImpl();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public MapEntryImpl newSerializable(Mailbox mailbox, Ancestor parent) throws Exception {
        MapEntryImpl me = (MapEntryImpl) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        Factory keyFactory = fl.getFactory(keyType);
        Factory valueFactory = fl.getFactory(valueType);
        me.setFactories(keyFactory, valueFactory);
        return me;
    }
}
