package org.agilewiki.pactor.utilImpl.durable.collection.smap;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.*;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.collection.MapEntryFactory;

/**
 * Creates LongSMap's.
 */
public class LongSMapFactory extends FactoryImpl {

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        registerFactory(_factoryLocator, PAMap.LONG_PASTRING_MAP, PAString.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_BYTES_MAP, Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_BOX_MAP, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_PALONG_MAP, PALong.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.LONG_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);
    }

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType,
                                       int initialCapacity) {
        factoryLocator.registerFactory(new MapEntryFactory(
                "E." + actorType, PALong.FACTORY_NAME, valueType));
        factoryLocator.registerFactory(new LongSMapFactory(
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
    public LongSMap newSerializable(Mailbox mailbox, Ancestor parent) {
        LongSMap imj = (LongSMap) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
