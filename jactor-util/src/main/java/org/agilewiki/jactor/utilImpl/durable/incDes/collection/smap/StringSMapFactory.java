package org.agilewiki.jactor.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.durable.incDes.PAString;
import org.agilewiki.jactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.MapEntryFactory;

/**
 * Creates StringSMap's.
 */
public class StringSMapFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String valueType,
                                       int initialCapacity) {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new MapEntryFactory(
                "E." + actorType, PAString.FACTORY_NAME, valueType));
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new StringSMapFactory(
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
    protected StringSMapFactory(String jidType, String valueType, int initialCapacity) {
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
    protected StringSMap instantiateActor() {
        return new StringSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public StringSMap newSerializable(Mailbox mailbox, Ancestor parent)
            throws Exception {
        StringSMap imj = (StringSMap) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
