package org.agilewiki.pactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.incDes.PALong;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.MapEntryFactory;

/**
 * Creates LongSMap's.
 */
public class LongSMapFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String valueType,
                                       int initialCapacity) {
        ((FactoryLocatorImpl)_factoryLocator).registerFactory(new MapEntryFactory(
                "E." + actorType, PALong.FACTORY_NAME, valueType));
        ((FactoryLocatorImpl)_factoryLocator).registerFactory(new LongSMapFactory(
                actorType, valueType, initialCapacity));
    }

    private String valueType;
    private int initialCapacity = 10;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType         The jid type.
     * @param valueType       The value type.
     * @param initialCapacity The initial capacity.
     */
    protected LongSMapFactory(String jidType, String valueType, int initialCapacity) {
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
    protected LongSMap instantiateActor() {
        return new LongSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public LongSMap newSerializable(Mailbox mailbox, Ancestor parent)
            throws Exception {
        LongSMap imj = (LongSMap) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
