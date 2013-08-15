package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.MapEntryFactory;

/**
 * Creates IntegerSMap's.
 */
public class IntegerSMapFactory extends FactoryImpl {

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) throws FactoryLocatorClosedException {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String actorType,
                                       String valueType,
                                       int initialCapacity) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new MapEntryFactory(
                "E." + actorType, JAInteger.FACTORY_NAME, valueType));
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new IntegerSMapFactory(
                actorType, valueType, initialCapacity));
    }

    private String valueType;
    private int initialCapacity = 10;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     */
    protected IntegerSMapFactory(String jidType, String valueType, int initialCapacity) {
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
    protected IntegerSMap instantiateActor() {
        return new IntegerSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param messageProcessor The processing of the new actor.
     * @param parent           The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public IntegerSMap newSerializable(MessageProcessor messageProcessor, Ancestor parent)
            throws Exception {
        IntegerSMap imj = (IntegerSMap) super.newSerializable(messageProcessor, parent);
        FactoryLocator fl = Durables.getFactoryLocator(messageProcessor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
